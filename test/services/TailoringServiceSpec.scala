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

package services

import models.errors.MongoError
import support.UnitTest
import support.mocks.{MockTailoringService, MockTailoringUserDataRepository}

class TailoringServiceSpec extends  UnitTest
  with MockTailoringUserDataRepository
  with MockTailoringService {

  private val anyTaxYear = 2022
  private val anyNino = "any-nino"

  private val underTest = new TailoringService(mockTailoringUserDataRepository)

  ".removeTailoringData(...) " should {

    "return error when tailoringUserDataRepository.clear(...) fails" in {
      mockClear(anyNino, anyTaxYear, result = Left(MongoError("some-error")))

      await(underTest.removeTailoringData(anyNino, anyTaxYear)) shouldBe Left(MongoError("some-error"))
    }

    "return success when tailoringUserDataRepository.clear(...) succeeded" in {
      mockClear(anyNino, anyTaxYear, result = Right(true))

      await(underTest.removeTailoringData(anyNino, anyTaxYear)) shouldBe Right(true)
    }

  }

}
