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
import models.errors.DataNotFoundError
import models.mongo.TailoringUserData
import models.tailoring._
import play.api.Logging
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import services.TailoringService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TailoringController @Inject()(authorisedAction: AuthorisedAction,
                                    tailoringService: TailoringService,
                                    cc: ControllerComponents)
                                   (implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  def getAll(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { _ =>
    tailoringService.getTailoringData(nino, taxYear).map {
      case Right(data) => Ok(Json.toJson(data.tailoring))
      case Left(DataNotFoundError) => NotFound
      case Left(_) => InternalServerError
    }
  }
  def getAboutYou(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { _ =>
    tailoringService.getTailoringData(nino, taxYear).map {
      case Right(data) => Ok(Json.toJson(data.tailoring.aboutYou))
      case Left(DataNotFoundError) => NotFound
      case Left(_) => InternalServerError
    }
  }
  def getWorkBenefit(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { _ =>
    tailoringService.getTailoringData(nino, taxYear).map {
      case Right(data) => Ok(Json.toJson(data.tailoring.workBenefits))
      case Left(DataNotFoundError) => NotFound
      case Left(_) => InternalServerError
    }
  }
  def getRentalsPensionsInvestments(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { _ =>
    tailoringService.getTailoringData(nino, taxYear).map {
      case Right(data) => Ok(Json.toJson(data.tailoring.rentalsPensionsInvestments))
      case Left(DataNotFoundError) => NotFound
      case Left(_) => InternalServerError
    }
  }
  def getGainsTrustsEstates(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { _ =>
    tailoringService.getTailoringData(nino, taxYear).map {
      case Right(data) => Ok(Json.toJson(data.tailoring.gainsTrustsEstates))
      case Left(DataNotFoundError) => NotFound
      case Left(_) => InternalServerError
    }
  }
  def getPensionsPayments(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { _ =>
    tailoringService.getTailoringData(nino, taxYear).map {
      case Right(data) => Ok(Json.toJson(data.tailoring.pensionsPayments))
      case Left(DataNotFoundError) => NotFound
      case Left(_) => InternalServerError
    }
  }
  def getAllowancesTaxRelief(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { _ =>
    tailoringService.getTailoringData(nino, taxYear).map {
      case Right(data) => Ok(Json.toJson(data.tailoring.allowancesTaxRelief))
      case Left(DataNotFoundError) => NotFound
      case Left(_) => InternalServerError
    }
  }

  def postAboutYou(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { implicit request =>
    val userData = request.request.body.asJson.map(_.validate[AboutYouModel]) match {
      case Some(JsSuccess(model, _)) =>
        Right(TailoringUserData(nino, taxYear, tailoring = TailoringDataModel(aboutYou = Some(model))))
      case _ =>
        logger.warn("[TailoringController][postAboutYou] Json invalid")
        Left(false)
    }
    userData match {
      case Left(_) => Future.successful(BadRequest)
      case Right(submittedData) =>
        handleCreateUpdate(submittedData)
    }
  }

  def postWorkBenefit(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { implicit request =>
    val userData = request.request.body.asJson.map(_.validate[WorkBenefitsModel]) match {
      case Some(JsSuccess(model, _)) =>
        Right(TailoringUserData(nino, taxYear, tailoring = TailoringDataModel(workBenefits = Some(model))))
      case _ =>
        logger.warn("[TailoringController][postWorkBenefit] Json invalid")
        Left(false)
    }
    userData match {
      case Left(_) => Future.successful(BadRequest)
      case Right(submittedData) =>
        handleCreateUpdate(submittedData)
    }
  }

  def postRentalsPensionsInvestments(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { implicit request =>
    val userData = request.request.body.asJson.map(_.validate[RentalsPensionsInvestmentsModel]) match {
      case Some(JsSuccess(model, _)) =>
        Right(TailoringUserData(nino, taxYear, tailoring = TailoringDataModel(rentalsPensionsInvestments = Some(model))))
      case _ =>
        logger.warn("[TailoringController][postRentalsPensionsInvestments] Json invalid")
        Left(false)
    }
    userData match {
      case Left(_) => Future.successful(BadRequest)
      case Right(submittedData) =>
        handleCreateUpdate(submittedData)
    }
  }

  def postGainsTrustsEstates(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { implicit request =>
    val userData = request.request.body.asJson.map(_.validate[GainsTrustsEstatesModel]) match {
      case Some(JsSuccess(model, _)) =>
        Right(TailoringUserData(nino, taxYear, tailoring = TailoringDataModel(gainsTrustsEstates = Some(model))))
      case _ =>
        logger.warn("[TailoringController][postGainsTrustsEstates] Json invalid")
        Left(false)
    }
    userData match {
      case Left(_) => Future.successful(BadRequest)
      case Right(submittedData) =>
        handleCreateUpdate(submittedData)
    }
  }

  def postPensionsPayments(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { implicit request =>
    val userData = request.request.body.asJson.map(_.validate[PensionsPaymentsModel]) match {
      case Some(JsSuccess(model, _)) =>
        Right(TailoringUserData(nino, taxYear, tailoring = TailoringDataModel(pensionsPayments = Some(model))))
      case _ =>
        logger.warn("[TailoringController][postPensionsPayments] Json invalid")
        Left(false)
    }
    userData match {
      case Left(_) => Future.successful(BadRequest)
      case Right(submittedData) =>
        handleCreateUpdate(submittedData)
    }
  }

  def postAllowancesTaxRelief(nino: String, taxYear: Int): Action[AnyContent] = authorisedAction.async { implicit request =>
    val userData = request.request.body.asJson.map(_.validate[AllowancesTaxReliefModel]) match {
      case Some(JsSuccess(model, _)) =>
        Right(TailoringUserData(nino, taxYear, tailoring = TailoringDataModel(allowancesTaxRelief = Some(model))))
      case _ =>
        logger.warn("[TailoringController][postAllowancesTaxRelief] Json invalid")
        Left(false)
    }
    userData match {
      case Left(_) => Future.successful(BadRequest)
      case Right(submittedData) =>
        handleCreateUpdate(submittedData)
    }
  }

  def handleCreateUpdate(newData: TailoringUserData): Future[Result] = {
    tailoringService.getTailoringData(newData.nino, newData.taxYear).map {
      case Right(previousData) =>
        tailoringService.updateCreateTailoringData(previousData.updateFrom(newData.tailoring))
        Created
      case Left(DataNotFoundError) => tailoringService.updateCreateTailoringData(newData)
        Created
      case Left(_) =>
        logger.warn("[TailoringController][handleCreateUpdate] Failed Create/Update TailoringData")
        InternalServerError
    }
  }

}
