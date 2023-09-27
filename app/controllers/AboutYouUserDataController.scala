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
import models.mongo.AboutYouUserData
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc.{Action, AnyContent, BodyParser, BodyParsers, ControllerComponents, Result}
import repositories.AboutYouUserDataRepository
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AboutYouUserDataController @Inject()(
                                    cc: ControllerComponents,
                                    authorisedAction: AuthorisedAction,
                                    repository: AboutYouUserDataRepository
                                  )(implicit ec: ExecutionContext)
  extends BackendController(cc) {

  def get(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { request =>
    // pull nino from user
      repository
        .get(nino, taxYear)
        .map {
          _.map(userData => Ok(Json.toJson(userData)))
            .getOrElse(NotFound)
        }
  }

  def set: Action[AnyContent] = authorisedAction.async { request =>
      request.body.asJson.map(_.validate[AboutYouUserData]) match {
        case Some(JsSuccess(model, _)) =>
          repository.set(model).map(_ => NoContent)
        case _ => Future.successful(BadRequest)
      }
  }

  def clear(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { request =>
      repository
        .clear(nino, taxYear)
        .map(_ => NoContent)
  }
}