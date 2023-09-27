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

import config.AppConfig
import models.mongo.AboutYouUserData
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.model._
import play.api.libs.json.Format
import uk.gov.hmrc.crypto.{Decrypter, Encrypter}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs.toBson
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.{Clock, Instant}
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AboutYouUserDataRepository @Inject()(
                                    mongoComponent: MongoComponent,
                                    appConfig: AppConfig,
                                    clock: Clock
                                  )(implicit ec: ExecutionContext, crypto: Encrypter with Decrypter)
  extends PlayMongoRepository[AboutYouUserData](
    collectionName = "About-You-User-Data",
    mongoComponent = mongoComponent,
    domainFormat   = AboutYouUserData.encryptedFormat,
    indexes        = Seq(
      IndexModel(
        Indexes.ascending("lastUpdated"),
        IndexOptions()
          .name("last-updated-index")
          .expireAfter(appConfig.mongoTTL, TimeUnit.DAYS)
      )
    )
  ) {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def filterByNinoYear(nino: String, taxYear: Int): Bson = and(
    equal("nino", toBson(nino)),
    equal("taxYear", toBson(taxYear))
  )

  def get(nino: String, taxYear: Int): Future[Option[AboutYouUserData]] =
        collection.find(filterByNinoYear(nino, taxYear))
          .headOption()

  def set(userData: AboutYouUserData): Future[Boolean] = {

    val updatedUserData = userData copy (lastUpdated = Instant.now(clock))

    collection
      .replaceOne(
        filter = filterByNinoYear(updatedUserData.nino, updatedUserData.taxYear),
        replacement = updatedUserData,
        options = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => true)
  }

  def clear(nino: String, taxYear: Int): Future[Boolean] =
    collection
      .deleteOne(filterByNinoYear(nino, taxYear))
      .toFuture()
      .map(_ => true)
}