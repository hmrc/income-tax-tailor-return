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

import models.errors.{ApiServiceError, DataNotFoundError}
import models.mongo.TailoringUserData
import models.tailoring.{PensionsPaymentsModel, TailoringDataModel}
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.Result
import support.ControllerUnitTest
import support.builders.mongo.TailoringModels.aPensionsPaymentsModel
import support.mocks.MockTailoringService

import scala.concurrent.Future

class PensionsPaymentsControllerSpec extends ControllerUnitTest
  with MockTailoringService {


  val underTest = new TailoringController(authorisedAction, mockTailoringService, cc)

  val nino = "A123459A"
  val taxYear = 2023

  ".getPensionsPayments" should {
    "return Ok with Data" in {

      mockAuth()
      mockGetAllTailoringData(nino, taxYear, Right(TailoringUserData(
        nino, taxYear, tailoring = TailoringDataModel(pensionsPayments = Some(aPensionsPaymentsModel)))))

      val result = underTest.getPensionsPayments(nino, taxYear)(fakeGetRequest)

      status(result) shouldBe OK
      bodyOf(result) shouldBe Json.toJson(aPensionsPaymentsModel).toString()
    }
    "return Not Found" in {
      mockAuth()
      mockGetAllTailoringData(nino, taxYear, Left(DataNotFoundError))

      val result = underTest.getPensionsPayments(nino, taxYear)(fakeGetRequest)

      status(result) shouldBe NOT_FOUND
    }
    "return Internal Server Error" in {
      mockAuth()
      mockGetAllTailoringData(nino, taxYear, Left(ApiServiceError("failed")))

      val result = underTest.getPensionsPayments(nino, taxYear)(fakeGetRequest)

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }
  ".postPensionsPayments" should {
    "return CREATED with prior Data" in {

      val tailoringModel = PensionsPaymentsModel(Some(true),Some(true),Some(true))
      val request = fakePostRequest.withJsonBody(Json.toJson(tailoringModel))

      mockAuth()
      mockGetAllTailoringData(nino, taxYear, Right(TailoringUserData(
        nino, taxYear, tailoring = TailoringDataModel(pensionsPayments = Some(PensionsPaymentsModel(Some(true),Some(true),Some(true)))))))
      mockCreate(TailoringUserData(nino, taxYear, TailoringDataModel(pensionsPayments = Some(tailoringModel))), Right(true))

      val result = underTest.postPensionsPayments(nino, taxYear)(request)

      status(result) shouldBe CREATED
    }
    "return CREATED when there is no priorData" in {

      val tailoringModel = PensionsPaymentsModel(Some(true),Some(true),Some(true))
      val request = fakePostRequest.withJsonBody(Json.toJson(tailoringModel))

      mockAuth()
      mockGetAllTailoringData(nino, taxYear, Left(DataNotFoundError))
      mockCreate(TailoringUserData(nino, taxYear, TailoringDataModel(pensionsPayments = Some(tailoringModel))), Right(true))

      val result = underTest.postPensionsPayments(nino, taxYear)(request)

      status(result) shouldBe CREATED
    }
    "return BadRequest when request body is invalid" in {

      val request = fakePostRequest.withJsonBody(Json.parse("""{"wrongFormat": "100"}"""))

      mockAuth()

      val result = underTest.postPensionsPayments(nino, taxYear)(request)

      status(result) shouldBe BAD_REQUEST
    }
    "return BadRequest when request body is Empty" in {

      val request = fakePostRequest

      mockAuth()

      val result = underTest.postPensionsPayments(nino, taxYear)(request)

      status(result) shouldBe BAD_REQUEST
    }
    "return INTERNAL_SERVER_ERROR when get priorData fails" in {

      val tailoringModel = PensionsPaymentsModel(Some(true), Some(true), Some(true))
      val request = fakePostRequest.withJsonBody(Json.toJson(tailoringModel))

      mockAuth()
      mockGetAllTailoringData(nino, taxYear, Left(ApiServiceError("failed")))


      val result: Future[Result] = underTest.postPensionsPayments(nino, taxYear)(request)

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

}
