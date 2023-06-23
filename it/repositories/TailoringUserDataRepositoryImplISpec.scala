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

import models.errors.{DataNotFoundError, EncryptionDecryptionError}
import models.mongo.{EncryptedTailoringUserData, TailoringUserData}
import org.joda.time.{DateTime, DateTimeZone}
import org.mongodb.scala.MongoWriteException
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.inject.guice.GuiceApplicationBuilder
import support.IntegrationTest
import support.builders.mongo.TailoringModels.aTailoringDataModel
import utils.AesGcmAdCrypto

class TailoringUserDataRepositoryImplISpec extends IntegrationTest {

  protected implicit val aesGcmAdCrypto: AesGcmAdCrypto = app.injector.instanceOf[AesGcmAdCrypto]

  private val nino = "AA123456A"

  private val repoWithInvalidEncryption = GuiceApplicationBuilder()
    .configure(config + ("mongodb.encryption.key" -> "key")).build()
    .injector.instanceOf[TailoringUserDataRepositoryImpl]

  private val underTest: TailoringUserDataRepositoryImpl = app.injector.instanceOf[TailoringUserDataRepositoryImpl]

  class EmptyDatabase {
    await(underTest.collection.drop().toFuture())
    await(underTest.ensureIndexes())
    await(underTest.collection.countDocuments().toFuture()) shouldBe 0
  }

  val testData: TailoringUserData = TailoringUserData(nino, taxYear, aTailoringDataModel)

  "the set indexes" should {
    "enforce uniqueness" in new EmptyDatabase {
      private val data_1: TailoringUserData = TailoringUserData(nino, taxYear, aTailoringDataModel)
      private val data_2: TailoringUserData = TailoringUserData(nino, taxYear, aTailoringDataModel.copy(aboutYou = None))

      implicit val associatedText: String = data_1.nino
      await(underTest.createOrUpdate(data_1))
      await(underTest.collection.countDocuments().toFuture()) shouldBe 1

      private val encryptedUserData: EncryptedTailoringUserData = data_2.encrypted

      private val caught = intercept[MongoWriteException](await(underTest.collection.insertOne(encryptedUserData).toFuture()))

      caught.getMessage must
        include("E11000 duplicate key error collection: income-tax-tailor-return.TailoringUserData index: UserDataLookupIndex dup key:")
    }
  }

  "createOrUpdate with invalid encryption" should {
    "fail to add data" in new EmptyDatabase {
      await(repoWithInvalidEncryption.createOrUpdate(testData)) shouldBe
        Left(EncryptionDecryptionError("Failed encrypting data"))
    }
  }

  "find with invalid encryption" should {
    "fail to find data" in new EmptyDatabase {
      implicit val associatedText: String = testData.nino
      await(underTest.collection.countDocuments().toFuture()) shouldBe 0
      await(repoWithInvalidEncryption.collection.insertOne(testData.encrypted).toFuture())
      await(underTest.collection.countDocuments().toFuture()) shouldBe 1
      await(repoWithInvalidEncryption.find(nino, testData.taxYear)) shouldBe
        Left(EncryptionDecryptionError("Failed encrypting data"))
    }
  }

  "handleEncryptionDecryptionException" should {
    "handle an exception" in {
      repoWithInvalidEncryption.handleEncryptionDecryptionException(new Exception("fail"), "") shouldBe Left(EncryptionDecryptionError("fail"))
    }
  }

  "createOrUpdate" should {

    "create a document in collection when one does not exist" in new EmptyDatabase {
      await(underTest.collection.countDocuments().toFuture()) shouldBe 0
      await(underTest.createOrUpdate(testData))
      await(underTest.collection.countDocuments().toFuture()) shouldBe 1
    }

    "update a document in collection when one already exists" in new EmptyDatabase {
      await(underTest.createOrUpdate(testData.copy(tailoring = aTailoringDataModel.copy(aboutYou = None)))) shouldBe Right(true)
      await(underTest.collection.countDocuments().toFuture()) shouldBe 1

      private val updatedUserData = testData.copy(tailoring = aTailoringDataModel.copy(aboutYou = None))

      await(underTest.createOrUpdate(updatedUserData)) shouldBe Right(true)
      await(underTest.collection.countDocuments().toFuture()) shouldBe 1
      await(underTest.find(nino, updatedUserData.taxYear)).map(_.tailoring) shouldBe Right(updatedUserData.tailoring)
    }
  }

  "find" should {
    "get a document and update the TTL" in new EmptyDatabase {
      private val now = DateTime.now(DateTimeZone.UTC)
      private val data = testData.copy(lastUpdated = now)

      await(underTest.createOrUpdate(data))
      await(underTest.collection.countDocuments().toFuture()) shouldBe 1

      private val result = await(underTest.find(nino, data.taxYear))

      result.map(_.copy(lastUpdated = data.lastUpdated)) shouldBe Right(data)
      result.map(_.lastUpdated.isAfter(data.lastUpdated)) shouldBe Right(true)
    }

    "return DataNotFoundError when find operation did not find data for the given inputs" in new EmptyDatabase {
      await(underTest.find(nino, taxYear)) shouldBe Left(DataNotFoundError)
    }
  }

  "clear" should {
    "remove a record" in new EmptyDatabase {
      await(underTest.collection.countDocuments().toFuture()) mustBe 0
      await(underTest.createOrUpdate(testData))
      await(underTest.collection.countDocuments().toFuture()) shouldBe 1

      await(underTest.clear(testData.nino, testData.taxYear)) mustBe Right(true)
      await(underTest.collection.countDocuments().toFuture()) mustBe 0
    }
  }

}
