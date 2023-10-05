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

import com.fasterxml.jackson.core.JsonParseException
import models.mongo.UserData
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters
import play.api.Configuration
import play.api.libs.json.Json
import support.IntegrationTest
import uk.gov.hmrc.crypto.{Decrypter, Encrypter, SymmetricCryptoFactory}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import java.security.SecureRandom
import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneId}
import java.util.Base64

class UserDataRepositorySpec
  extends IntegrationTest
    with DefaultPlayMongoRepositorySupport[UserData] {

  private val instant = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock: Clock = Clock.fixed(instant, ZoneId.systemDefault)

  private val userData = UserData("mtdItId", 2023, Json.obj("foo" -> "bar"), Instant.ofEpochSecond(1))

  private val aesKey = {
    val aesKey = new Array[Byte](32)
    new SecureRandom().nextBytes(aesKey)
    Base64.getEncoder.encodeToString(aesKey)
  }

  private val configuration = Configuration("crypto.key" -> aesKey)

  private implicit val crypto: Encrypter with Decrypter =
    SymmetricCryptoFactory.aesGcmCryptoFromConfig("crypto", configuration.underlying)

  protected override val repository = new UserDataRepository(
    mongoComponent = mongoComponent,
    appConfig = appConfig,
    clock = stubClock
  )(ec, crypto)

  def filterByMtdItIdYear(mtdItId: String, taxYear: Int) = Filters.and(Filters.equal("mtdItId", mtdItId),Filters.equal("taxYear", taxYear))

  ".set" should  {

    "must set the last updated time on the supplied user answers to `now`, and save them" in {

      val expectedResult = userData copy (lastUpdated = instant)

      val setResult = repository.set(userData).futureValue
      val updatedRecord = find(filterByMtdItIdYear(userData.mtdItId, userData.taxYear)).futureValue.headOption

      setResult shouldBe ""
      updatedRecord shouldBe ""
    }

    "must store the data section as encrypted bytes" in {

      repository.set(userData).futureValue

      val record = repository.collection
        .find[BsonDocument](filterByMtdItIdYear(userData.mtdItId, userData.taxYear))
        .headOption()
        .futureValue
        .get

      val json = Json.parse(record.toJson)
      val data = (json \ "data").as[String]

      assertThrows[JsonParseException] {
        Json.parse(data)
      }
    }
  }

//  ".get" when {
//
//    "when there is a record for this id" should {
//
//      "must update the lastUpdated time and get the record" in {
//
//        insert(userData).futureValue
//
//        val result = repository.get(userData.id).futureValue
//        val expectedResult = userData copy (lastUpdated = instant)
//
//        result.value mustEqual expectedResult
//      }
//    }
//
//    "when there is no record for this id" should {
//
//      "must return None" in {
//
//        repository.get("id that does not exist").futureValue must not be defined
//      }
//    }
//  }
//
//  ".clear" should {
//
//    "must remove a record" in {
//
//      insert(userData).futureValue
//
//      val result = repository.clear("mtdItId", 2023).futureValue
//
//      result shouldBe
//      repository.get("mtdItId", 2023).futureValue must not be defined
//    }
//
//    "must return Done when there is no record to remove" in {
//      val result = repository.clear("id that does not exist", 2023).futureValue
//
//      result shouldBe "fail"
//    }
//  }

}
