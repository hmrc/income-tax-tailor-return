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

import models.TaxYearPathBindable.TaxYear
import org.mockito.ArgumentMatchers.any
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
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.~

import java.time.{LocalDate, ZoneId}
import scala.concurrent.Future

class TaskListDataControllerSpec
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
    new~(
      Some(AffinityGroup.Individual),
      enrolments)

  private val mockAuthConnector = mock[AuthConnector]

  private val taxYear = TaxYear(LocalDate.now(ZoneId.systemDefault()).getYear + 1)
  private val userData = Json.obj(
    "mtdItId" -> "1234567890",
    "taxYear" -> taxYear.taxYear,
    "data" -> Json.obj(
      "tasks" -> Json.arr(
        ".aboutYou" -> Json.arr(
          Json.obj(
            "title" -> Json.obj(
              "html" -> "Residence status",
              "classes" -> ""
            ),
            "status" -> Json.obj(
              "html" -> "Completed",
              "classes" -> ""
            ),
            "href" -> "#",
            "classes" -> ""
          )
        )
      )
    ),
    "lastUpdated" -> Json.obj(
      "$date" -> Json.obj(
        "$numberLong" -> "1714565277082"
      )
    )
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  private val app = new GuiceApplicationBuilder().overrides(bind[AuthConnector].toInstance(mockAuthConnector)).build()

  when(mockAuthConnector.authorise[Option[AffinityGroup] ~ Enrolments](any(), any())(any(), any()))
    .thenReturn(Future.successful(authResponse))

  ".get" should {

    "return OK and the data when user data can be found for this mtdItId and taxYear" in {

      val request =
        FakeRequest(GET, routes.TaskListDataController.get(taxYear).url)
          .withHeaders("mtditid" -> "1234567890")

      val result = route(app, request).value

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(userData)
    }
  }
}