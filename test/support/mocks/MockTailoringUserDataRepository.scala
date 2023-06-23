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
import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import repositories.TailoringUserDataRepository

import scala.concurrent.Future

trait MockTailoringUserDataRepository extends MockFactory {

  protected val mockTailoringUserDataRepository: TailoringUserDataRepository = mock[TailoringUserDataRepository]

  def mockClear(nino: String, taxYear: Int,
                result: Either[ServiceError, Boolean]): CallHandler2[String, Int, Future[Either[ServiceError, Boolean]]] = {
    (mockTailoringUserDataRepository.clear(_: String, _: Int))
      .expects(nino, taxYear)
      .returning(Future.successful(result))
  }
}
