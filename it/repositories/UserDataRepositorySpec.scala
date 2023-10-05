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
import models.Done
import models.mongo.UserData
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters
import org.scalamock.scalatest.MockFactory
import org.scalatest.OptionValues
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
    with OptionValues
    with DefaultPlayMongoRepositorySupport[UserData]
    with MockFactory {

  private val instant = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock: Clock = Clock.fixed(instant, ZoneId.systemDefault)

  private val userData = UserData("mtdItId", 2023, Json.obj("foo" -> "bar"), Instant.ofEpochSecond(1))
  private val invalidTaxYear = 1999

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
  )

  def filterByMtdItIdYear(mtdItId: String, taxYear: Int): Bson = Filters.and(Filters.equal("mtdItId", mtdItId),Filters.equal("taxYear", taxYear))

  ".set" should  {

    "set the last updated time on the supplied user answers to `now`, and save them" in {

      val expectedResult = userData copy (lastUpdated = instant)

      val setResult = repository.set(userData).futureValue
      val updatedRecord = find(filterByMtdItIdYear(userData.mtdItId, userData.taxYear)).futureValue.headOption.value

      setResult shouldBe Done
      updatedRecord shouldBe expectedResult
    }

    "store the data section as encrypted bytes" in {

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

  ".get" should {

    "update the lastUpdated time and get the record" when {

      "there is a record for this mtdItId" in {

        insert(userData).futureValue

        val result = repository.get(userData.mtdItId, userData.taxYear).futureValue
        val expectedResult = userData copy (lastUpdated = instant)
        println(Json.toJson(userData).toString())

        result.value shouldBe expectedResult
      }
    }

    "return None" when {

      "there is no record for this mtdItId" in {

        repository.get("mtdItId that does not exist", userData.taxYear).futureValue should not be defined
      }

      "there is no record for this taxYear" in {

        repository.get(userData.mtdItId, invalidTaxYear).futureValue should not be defined
      }
    }
  }

  ".clear" should {

    "remove a record" in {

      insert(userData).futureValue

      val result = repository.clear(userData.mtdItId, userData.taxYear).futureValue

      result shouldBe Done
      repository.get(userData.mtdItId, userData.taxYear).futureValue should not be defined
    }

    "should return Done when there is no record for the mtdItId to remove" in {
      val result = repository.clear("mtdItId that does not exist", userData.taxYear).futureValue

      result shouldBe Done
    }

    "should return Done when there is no record for the TaxYear to remove" in {
      val result = repository.clear(userData.mtdItId, invalidTaxYear).futureValue

      result shouldBe Done
    }
  }


  ".keepAlive" should {

    "update its lastUpdated to `now` and return Done" when {

      "there is a record for this mtdItId" in {

        insert(userData).futureValue

        val result = repository.keepAlive(userData.mtdItId, userData.taxYear).futureValue

        val expectedUpdatedAnswers = userData copy (lastUpdated = instant)

        result shouldBe Done
        val updatedAnswers = find(filterByMtdItIdYear(userData.mtdItId, userData.taxYear)).futureValue.headOption.value
        updatedAnswers shouldBe expectedUpdatedAnswers
      }
    }

    "when there is no record for this mtdItId" should {

      "return Done" in {

        repository.keepAlive("id that does not exist", 2023).futureValue shouldBe Done
      }
    }
    "when there is no record for this taxYear" should {

      "return Done" in {

        repository.keepAlive(userData.mtdItId, invalidTaxYear).futureValue shouldBe Done
      }
    }
  }

}
