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

package controllers

import models.Done
import models.TaxYearPathBindable.TaxYear
import models.mongo.UserData
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.UserDataRepository
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core._

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneId}
import scala.concurrent.Future

class UserDataControllerSpec
  extends AnyWordSpec
    with Matchers
    with MockitoSugar
    with OptionValues
    with ScalaFutures
    with BeforeAndAfterEach {

  private val enrolments: Enrolments = Enrolments(Set(
    Enrolment(models.Enrolment.MtdIncomeTax.key,
      Seq(EnrolmentIdentifier(models.Enrolment.MtdIncomeTax.value, "1234567890")), "Activated")
  ))

  private val authResponse: Some[AffinityGroup] ~ Enrolments =
    new ~(
      Some(AffinityGroup.Individual),
      enrolments)

  private val mockRepo = mock[UserDataRepository]
  private val mockAuthConnector = mock[AuthConnector]

  private val instant = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock = Clock.fixed(instant, ZoneId.systemDefault)
  private val userData = UserData("1234567890", 2023, Json.obj("bar" -> "baz"), Instant.now(stubClock))
  private val taxYear = TaxYear(userData.taxYear)
  private val invalidTaxYear = TaxYear(1899)


  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  private val app = new GuiceApplicationBuilder().overrides(bind[UserDataRepository].toInstance(mockRepo),
    bind[AuthConnector].toInstance(mockAuthConnector)).build()

  when(mockAuthConnector.authorise[Option[AffinityGroup] ~ Enrolments](any(), any())(any(), any()))
    .thenReturn(Future.successful(authResponse))

  ".get" should {

    "return OK and the data when user data can be found for this mtdItId and taxYear" in {

      when(mockRepo.get(eqTo(userData.mtdItId), eqTo(userData.taxYear))).thenReturn(Future.successful(Some(userData)))

        val request =
          FakeRequest(GET, routes.UserDataController.get(taxYear).url)
            .withHeaders("mtditid" -> userData.mtdItId)

        val result = route(app, request).value

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(userData)
    }

    "return NOT_FOUND when user data cannot be found for this mtditid and taxYear" in {

      when(mockRepo.get(any(), any())) thenReturn Future.successful(None)

      val request =
        FakeRequest(GET, routes.UserDataController.get(taxYear).url)
          .withHeaders("mtditid" -> userData.mtdItId)

      val result = route(app, request).value

      status(result) shouldBe NOT_FOUND
    }

    "return UNAUTHORIZED when the request does not have a mtditid in their header" in {

      val request = FakeRequest(GET, routes.UserDataController.get(taxYear).url)

      val result = route(app, request).value

      status(result) shouldBe UNAUTHORIZED
    }

    "return BAD_REQUEST when the taxYear is not valid" in {

      val request = FakeRequest(GET, routes.UserDataController.get(invalidTaxYear).url)
        .withHeaders("mtditid" -> userData.mtdItId)

      val result = route(app, request).value

      status(result) shouldBe BAD_REQUEST
    }
  }


    ".set" should {

      "return No Content when the data is successfully saved" in {

        when(mockRepo.set(any())) thenReturn Future.successful(Done)

        val request =
          FakeRequest(POST, routes.UserDataController.set.url)
            .withHeaders(
              "mtditid" -> userData.mtdItId,
              "Content-Type"         -> "application/json"
            )
            .withBody(Json.toJson(userData).toString)

        val result = route(app, request).value

        status(result) shouldBe NO_CONTENT
        verify(mockRepo, times(1)).set(eqTo(userData))
      }
      "return Bad Request when the taxYear is invalid" in {

        when(mockRepo.set(any())) thenReturn Future.successful(Done)

        val request =
          FakeRequest(POST, routes.UserDataController.set.url)
            .withHeaders(
              "mtditid" -> userData.mtdItId,
              "Content-Type"         -> "application/json"
            )
            .withBody(Json.toJson(userData.copy(taxYear = 1999)).toString)

        val result = route(app, request).value

        status(result) shouldBe BAD_REQUEST
      }

      "return UNAUTHORIZED when the request does not have a mtditid in request header" in {

        val request =
          FakeRequest(POST, routes.UserDataController.set.url)
            .withHeaders("Content-Type" -> "application/json")
            .withBody(Json.toJson(userData))

        val result = route(app, request).value

        status(result) shouldBe UNAUTHORIZED
      }

      "return Bad Request when the request cannot be parsed as UserData" in {

        val badPayload = Json.obj("foo" -> "bar")

        val request =
          FakeRequest(POST, routes.UserDataController.set.url)
            .withHeaders(
              "mtditid" -> userData.mtdItId,
              "Content-Type"         -> "application/json"
            )
            .withBody(badPayload)

        val result = route(app, request).value

        status(result) shouldBe BAD_REQUEST
      }
    }

    ".clear" should {

      "return No Content when data is cleared" in {

        when(mockRepo.clear(eqTo(userData.mtdItId), eqTo(userData.taxYear))) thenReturn Future.successful(Done)

        val request =
          FakeRequest(DELETE, routes.UserDataController.clear(taxYear).url)
            .withHeaders("mtditid" -> userData.mtdItId)

        val result = route(app, request).value

        status(result) shouldBe NO_CONTENT
        verify(mockRepo, times(1)).clear(eqTo(userData.mtdItId), eqTo(userData.taxYear))
      }

      "return UNAUTHORIZED when the request does not have a mtditid in request header" in {

        val request =
          FakeRequest(DELETE, routes.UserDataController.clear(taxYear).url)
            .withBody(Json.toJson(userData))

        val result = route(app, request).value

        status(result) shouldBe UNAUTHORIZED
      }
      "return BAD_REQUEST when the request does taxYear is invalid" in {

        val request =
          FakeRequest(DELETE, routes.UserDataController.clear(invalidTaxYear).url)
            .withBody(Json.toJson(userData))

        val result = route(app, request).value

        status(result) shouldBe BAD_REQUEST
      }
    }
    ".keepAlive" should {

      "return No Content when keepAlive updates last updated" in {

        when(mockRepo.keepAlive(eqTo(userData.mtdItId), eqTo(userData.taxYear))) thenReturn Future.successful(Done)

        val request =
          FakeRequest(POST, routes.UserDataController.keepAlive(taxYear).url)
            .withHeaders("mtditid" -> userData.mtdItId)

        val result = route(app, request).value

        status(result) shouldBe NO_CONTENT
        verify(mockRepo, times(1)).clear(eqTo(userData.mtdItId), eqTo(userData.taxYear))
      }

      "return UNAUTHORIZED when the request does not have a mtditid in request header" in {

        val request =
          FakeRequest(POST, routes.UserDataController.keepAlive(taxYear).url)
            .withBody(Json.toJson(userData))

        val result = route(app, request).value

        status(result) shouldBe UNAUTHORIZED
      }
      "return BAD_REQUEST when the request does taxYear is invalid" in {

        val request =
          FakeRequest(POST, routes.UserDataController.keepAlive(invalidTaxYear).url)
            .withBody(Json.toJson(userData))

        val result = route(app, request).value

        status(result) shouldBe BAD_REQUEST
      }
    }
}