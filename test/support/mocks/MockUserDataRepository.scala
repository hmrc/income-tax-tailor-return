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

package support.mocks

import models.errors.ServiceError
import models.mongo.TailoringUserData
import org.scalamock.function.FunctionAdapter1
import org.scalamock.handlers.{CallHandler1, CallHandler2}
import org.scalamock.scalatest.MockFactory
import repositories.TailoringUserDataRepository

import java.util.UUID
import scala.concurrent.Future

trait MockUserDataRepository extends MockFactory {

  protected val mockUserDataRepository: TailoringUserDataRepository = mock[TailoringUserDataRepository]


  def toTolerantMatcher(expected: TailoringUserData): FunctionAdapter1[TailoringUserData, Boolean] =
    where {
      (actual: TailoringUserData) => expected.nino.equals(actual.nino) && expected.taxYear == actual.taxYear && expected.tailoring == actual.tailoring
    }


  def mockCreateOrUpdate(userData: TailoringUserData,
                         result: Either[ServiceError, Boolean]): CallHandler1[TailoringUserData, Future[Either[ServiceError, Boolean]]] = {
    (mockUserDataRepository.createOrUpdate(_: TailoringUserData))
      .expects(toTolerantMatcher(userData))
      .returning(Future.successful(result))
  }

  def mockFind(nino: String,
               taxYear: Int,
               result: Either[ServiceError, TailoringUserData]): CallHandler2[String, Int, Future[Either[ServiceError, TailoringUserData]]] = {
    (mockUserDataRepository.find(_: String, _: Int))
      .expects(nino, taxYear)
      .returning(Future.successful(result))
  }
}
