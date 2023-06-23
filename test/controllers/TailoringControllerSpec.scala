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

import controllers.predicates.AuthorisedAction
import models.errors.{DataNotFoundError, DataNotUpdatedError}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, NO_CONTENT}
import support.ControllerUnitTest
import support.mocks.MockTailoringService
import support.providers.FakeRequestProvider
import support.utils.TaxYearUtils.taxYear

class TailoringControllerSpec extends ControllerUnitTest
  with MockTailoringService
  with FakeRequestProvider {

  private val nino = "AA123456A"
  protected val mockAuthorisedAction: AuthorisedAction = new AuthorisedAction()(mockAuthConnector, defaultActionBuilder, cc)
  private val underTest = new TailoringController(mockAuthorisedAction, mockTailoringService, cc)

  ".removeTailoringData" should {
    "return NotFound when tailoringService.removeTailoringData(...) returns DataNotFoundError" in {
      mockAuth()
      mockRemoveTailoringData(nino, taxYear, Left(DataNotFoundError))

      val result = underTest.removeTailoringData(nino, taxYear)(fakeDeleteRequest)

      status(result) shouldBe NOT_FOUND
    }

    "return InternalServerError when stateBenefitsService.removeClaim(...) returns any error different than DataNotFoundError" in {
      mockAuth()
      mockRemoveTailoringData(nino, taxYear, Left(DataNotUpdatedError))

      val result = underTest.removeTailoringData(nino, taxYear)(fakeDeleteRequest)

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }

    "return NoContent when stateBenefitsService.removeClaim(...) returns success" in {
      mockAuth()
      mockRemoveTailoringData(nino, taxYear, Right(true))

      val result = underTest.removeTailoringData(nino, taxYear)(fakeDeleteRequest)

      status(result) shouldBe NO_CONTENT
    }
  }
}
