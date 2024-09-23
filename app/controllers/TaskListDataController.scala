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

import controllers.predicates.IdentifierAction
import models.TaxYearPathBindable.TaxYear
import models.mongo.TaskListData
import play.api.Logging
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import repositories.TaskListDataRepository
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TaskListDataController @Inject()(
  cc: ControllerComponents,
  authorisedAction: IdentifierAction,
  repository: TaskListDataRepository
)(implicit ec: ExecutionContext)
  extends BackendController(cc) with Logging {

  def get(taxYear: TaxYear): Action[AnyContent] = authorisedAction.async { request =>
      repository
        .get(request.mtditid, taxYear.taxYear)
        .map {
          case Some(value) => Ok(Json.toJson(value.data)) //{} TODO empty??
          case None =>
            logger.warn("[TaskListDataController.get] No existing data returning Not Found")
            NotFound
        }.recover {
      case e =>
        logger.error(s"[TaskListDataController.get] recovered from: ${e.getMessage}")
        InternalServerError
    }
  }

  def set: Action[AnyContent] = authorisedAction.async { request =>
    (request.body.asJson.map(_.validate[TaskListData]) match {
      case Some(JsSuccess(model, _)) =>
        repository.set(model).map(_ => NoContent)
      case _ => Future.successful(BadRequest)
    }).recover {
      case e =>
        logger.error(s"[TaskListDataController.set] recovered from: ${e.getMessage}")
        InternalServerError
    }
  }

  def keepAlive(taxYear: TaxYear): Action[AnyContent] = authorisedAction.async {
    request =>
      repository
        .keepAlive(request.mtditid, taxYear.taxYear)
        .map(_ => NoContent)
  }

  def clear(taxYear: TaxYear): Action[AnyContent] = authorisedAction.async { request =>
    repository
      .clear(request.mtditid, taxYear.taxYear)
      .map(_ => NoContent)
  }
}
