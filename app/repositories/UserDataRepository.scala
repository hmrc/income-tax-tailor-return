/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package repositories

import com.google.inject.ImplementedBy
import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.Updates.set
import config.AppConfig
import models.errors.{DataNotFoundError, DataNotUpdatedError, MongoError, ServiceError}
import models.mongo._
import org.joda.time.{DateTime, DateTimeZone}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.{FindOneAndReplaceOptions, FindOneAndUpdateOptions}
import play.api.Logging
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs.toBson
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import utils.AesGcmAdCrypto
import utils.PagerDutyHelper.PagerDutyKeys._
import utils.PagerDutyHelper.pagerDutyLog

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class TailoringUserDataRepositoryImpl @Inject()(mongo: MongoComponent, appConfig: AppConfig)
                                                   (implicit aesGcmAdCrypto: AesGcmAdCrypto, ec: ExecutionContext)
  extends PlayMongoRepository[EncryptedTailoringUserData](
    mongoComponent = mongo,
    collectionName = "TailoringUserData",
    domainFormat = EncryptedTailoringUserData.formats,
    indexes = RepositoryIndexes.indexes()(appConfig),
    replaceIndexes = true
  ) with Repository with TailoringUserDataRepository with Logging {

  private lazy val findMessageStart = "[TailoringUserDataRepositoryRepositoryImpl][find]"
  private lazy val createOrUpdateMessageStart = "[TailoringUserDataRepositoryRepositoryImpl][create/update]"

  override def createOrUpdate(tailoringUserData: TailoringUserData): Future[Either[ServiceError, Boolean]] = {
    val userData = tailoringUserData.copy(lastUpdated = DateTime.now(DateTimeZone.UTC))

    encryptedFrom(userData) match {
      case Left(error: ServiceError) => Future.successful(Left(error))
      case Right(encryptedData) => createOrUpdateFrom(encryptedData)
    }
  }

  override def find(nino: String, taxYear: Int): Future[Either[ServiceError, TailoringUserData]] = {
    findBy(nino, taxYear).map {
      case Left(error) => Left(error)
      case Right(encryptedData) => decryptedFrom(encryptedData)
    }
  }

  private def createOrUpdateFrom(encryptedData: EncryptedTailoringUserData): Future[Either[ServiceError, Boolean]] = {
    val queryFilter: Bson = filter(encryptedData.nino, encryptedData.taxYear)
    val options = FindOneAndReplaceOptions().upsert(true).returnDocument(ReturnDocument.AFTER)
    collection.findOneAndReplace(queryFilter, encryptedData, options).toFutureOption().map {
      case Some(data) => Right(true)
      case None =>
        pagerDutyLog(FAILED_TO_CREATE_UPDATE_TAILORING_DATA, s"$createOrUpdateMessageStart Failed to update user data.")
        Left(DataNotUpdatedError)
    }.recover {
      case throwable: Throwable =>
        pagerDutyLog(FAILED_TO_CREATE_UPDATE_TAILORING_DATA, s"$createOrUpdateMessageStart Failed to update user data. Exception: ${throwable.getMessage}")
        Left(MongoError(throwable.getMessage))
    }
  }

  private def findBy(nino: String, taxYear: Int): Future[Either[ServiceError, EncryptedTailoringUserData]] = {
    val update = set("lastUpdated", toBson(DateTime.now(DateTimeZone.UTC))(EncryptedTailoringUserData.mongoJodaDateTimeFormats))
    val options = FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
    val eventualResult = collection.findOneAndUpdate(filter(nino, taxYear), update, options).toFutureOption().map {
      case Some(data) => Right(data)
      case None => Left(DataNotFoundError)
    }

    eventualResult.recover {
      case exception: Exception =>
        pagerDutyLog(FAILED_TO_FIND_TAILORING_DATA, s"$findMessageStart Failed to find user data. Exception: ${exception.getMessage}")
        Left(MongoError(exception.getMessage))
    }
  }

  private def decryptedFrom(encryptedData: EncryptedTailoringUserData): Either[ServiceError, TailoringUserData] = {
    implicit lazy val associatedText: String = encryptedData.nino
    Try(encryptedData.decrypted).toEither match {
      case Left(throwable: Throwable) => handleEncryptionDecryptionException(throwable.asInstanceOf[Exception], findMessageStart)
      case Right(decryptedData) => Right(decryptedData)
    }
  }

  private def encryptedFrom(userData: TailoringUserData): Either[ServiceError, EncryptedTailoringUserData] = {
    implicit val associatedText: String = userData.nino
    Try(userData.encrypted).toEither match {
      case Left(throwable: Throwable) => handleEncryptionDecryptionException(throwable.asInstanceOf[Exception], createOrUpdateMessageStart)
      case Right(encryptedData) => Right(encryptedData)
    }
  }

}

@ImplementedBy(classOf[TailoringUserDataRepositoryImpl])
trait TailoringUserDataRepository {
  def createOrUpdate(userData: TailoringUserData): Future[Either[ServiceError, Boolean]]

  def find(nino: String, taxYear: Int): Future[Either[ServiceError, TailoringUserData]]

}
