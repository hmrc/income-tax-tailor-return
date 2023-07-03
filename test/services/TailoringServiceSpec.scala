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

import models.errors.{DataNotFoundError, DataNotUpdatedError}
import models.mongo.TailoringUserData
import support.ControllerUnitTest
import support.builders.mongo.TailoringModels.aTailoringDataModel
import support.mocks.MockUserDataRepository

import scala.concurrent.Future

class TailoringServiceSpec extends ControllerUnitTest with MockUserDataRepository{

  private val underTest = new TailoringService(
    mockUserDataRepository
  )

  private val nino = "A123459A"
  private val taxYear = 2023

  ".getTailoringData" should {
    "return a model" in {

      val userData = TailoringUserData(nino, taxYear, aTailoringDataModel)

      mockFind(nino, taxYear, Right(userData))

      val result = await(underTest.getTailoringData(nino, taxYear))
      result shouldBe Right(userData)
    }
    "return an error" in {

      mockFind(nino, taxYear, Left(DataNotFoundError))

      val result = await(underTest.getTailoringData(nino, taxYear))
      result shouldBe Left(DataNotFoundError)
    }
  }

  ".updateCreateTailoringData" should {
    "return a a true when successful" in {

      mockCreateOrUpdate(TailoringUserData(nino, taxYear, aTailoringDataModel), Right(true))

      val result = await(underTest.updateCreateTailoringData(TailoringUserData(nino, taxYear, aTailoringDataModel)))
      result shouldBe Right(true)
    }
    "return an error" in {

      mockCreateOrUpdate(TailoringUserData(nino, taxYear, aTailoringDataModel), Left(DataNotUpdatedError))

      val result = await(underTest.updateCreateTailoringData(TailoringUserData(nino, taxYear, aTailoringDataModel)))
      result shouldBe Left(DataNotUpdatedError)
    }
  }

}
