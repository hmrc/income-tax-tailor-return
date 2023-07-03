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

import models.errors.ServiceError
import models.mongo.TailoringUserData
import repositories.TailoringUserDataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class TailoringService @Inject()(
                                   tailoringRepository: TailoringUserDataRepository
                                ) {

  def getTailoringData(nino: String, taxYear: Int): Future[Either[ServiceError, TailoringUserData]] = {
    tailoringRepository.find(nino, taxYear)
  }

  def updateCreateTailoringData(tailoringData: TailoringUserData): Future[Either[ServiceError, Boolean]] = {
    tailoringRepository.createOrUpdate(tailoringData)
  }
}
