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
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TaskListDataController @Inject()(cc: ControllerComponents,
                                       authorisedAction: IdentifierAction)
                                      (implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  def get(taxYear: TaxYear): Action[AnyContent] = authorisedAction.async { request =>
    // TODO Will return static model until we implement the logic and database storage of task list data
    Future.successful(Ok(
      Json.obj(
        "mtdItId" -> "1234567890",
        "taxYear" -> 2025,
        "data" -> Json.obj(
          "tasks" -> Json.arr(
            ".aboutYou" -> Json.arr(
              Json.obj(
                "title" -> Json.obj(
                  "html" -> "Residence status",
                  "classes" -> ""
                ),
                "status" -> Json.obj(
                  "html" -> "Completed",
                  "classes" -> ""
                ),
                "href" -> "#",
                "classes" -> ""
              )
            )
          )
        ),
        "lastUpdated" -> Json.obj(
          "$date" -> Json.obj(
            "$numberLong" -> "1714565277082"
          )
        )
      )
    )
    )
  }
}
