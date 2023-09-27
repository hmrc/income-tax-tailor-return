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

import models.{APIErrorBodyModel, APIErrorModel}
import models.errors.{ApiServiceError, DataNotFoundError}
import models.mongo.AboutYouUserData
import models.tailoring.TailoringDataModel
import org.scalamock.handlers.CallHandler3
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.Status
import services.EmploymentService
import support.ControllerUnitTest
import support.builders.mongo.TailoringModels.aAboutYou
import support.mocks.MockTailoringService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class EmploymentControllerSpec extends ControllerUnitTest {

  val mockService: EmploymentService = mock[EmploymentService]
  val underTest: EmploymentController = new EmploymentController(authorisedAction, mockService, cc)
  val nino = "A123459A"
  val taxYear = 2023

  def mockGetEmploymentData(
                             nino: String,
                             taxYear: Int,
                             result: Either[APIErrorModel, Option[Set[String]]]
                           )(implicit hc: HeaderCarrier): CallHandler3[String, Int, HeaderCarrier, Future[Either[APIErrorModel, Option[Set[String]]]]] = {
    (mockService.getEmploymentData(_: String, _: Int)(_: HeaderCarrier))
      .expects(nino, taxYear, *)
      .returning(Future.successful(result))
  }

  ".getEmploymentData" should {
    "return Ok with Data" in {

      mockAuth()
      mockGetEmploymentData(nino, taxYear, Right(Some(Set("Employer Name"))))

      val result = underTest.getEmploymentData(nino, taxYear)(fakeGetRequest)

      status(result) shouldBe OK
      bodyOf(result) shouldBe Json.toJson(Set("Employer Name")).toString()
    }

    "return Ok with No Data" in {

      mockAuth()
      mockGetEmploymentData(nino, taxYear, Right(None))

      val result = underTest.getEmploymentData(nino, taxYear)(fakeGetRequest)

      status(result) shouldBe OK
      bodyOf(result) shouldBe Json.toJson(Set[String]()).toString()
    }

    "return Not Found" in {

      mockAuth()
      mockGetEmploymentData(nino, taxYear, Left(APIErrorModel(NOT_FOUND, APIErrorBodyModel("NOT_FOUND", "No data found for user."))))

      val result = underTest.getEmploymentData(nino, taxYear)(fakeGetRequest)

      status(result) shouldBe NOT_FOUND
    }
  }
}
