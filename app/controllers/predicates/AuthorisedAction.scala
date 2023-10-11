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

package controllers.predicates

import models.User
import play.api.Logging
import play.api.mvc.Results.Unauthorized
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.{affinityGroup, allEnrolments}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import models.Enrolment

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[User, AnyContent] with ActionFunction[Request, User]

class AuthorisedAction @Inject()(
                                               override val authConnector: AuthConnector,
                                               val parser: BodyParsers.Default
                                             )
                                             (implicit val executionContext: ExecutionContext)
  extends IdentifierAction with AuthorisedFunctions with Logging {

  private val unauthorized: Future[Result] = Future.successful(Unauthorized)
  private case class AgentDetails(mtdId: String, arn: String)

  private def authorisedForMtdItId(enrolments: Enrolments): Option[String] = {
    for {
      enrolment <- enrolments.getEnrolment(Enrolment.MtdIncomeTax.key)
      id <- enrolment.getIdentifier(Enrolment.MtdIncomeTax.value)
    } yield id.value
  }
  private def authorisedForMtdItId(mtditid: String, enrolments: Enrolments): Option[String] = {
    enrolments.enrolments.find(x => x.identifiers.exists(i => i.value.equals(mtditid)))
      .flatMap(_.getIdentifier(Enrolment.MtdIncomeTax.value)).map(_.value)
  }

  private def authorisedAgentForMtdItId(mtditid: String, enrolments: Enrolments): Option[AgentDetails] = {
    //  todo possible check for "mtd-it-auth" rule
    for {
      mtdId <- authorisedForMtdItId(mtditid, enrolments)
      agentEnrolment <- enrolments.getEnrolment(Enrolment.Agent.key)
      arn <- agentEnrolment.getIdentifier(Enrolment.Agent.value)
    } yield AgentDetails(mtdId, arn.value)
  }

  override def invokeBlock[A](request: Request[A], block: User[A] => Future[Result]): Future[Result] = {

    implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    request.headers.get("mtditid").fold {
      logger.warn("[AuthorisedAction][async] - No MTDITID in the header. Returning unauthorised.")
      unauthorized
    }(
      mtdItId =>
        authorised().retrieve(affinityGroup and allEnrolments) {
          case Some(AffinityGroup.Individual) ~ enrolments if authorisedForMtdItId(enrolments).contains(mtdItId) =>
            block(User(mtdItId, None)(request))
          case Some(AffinityGroup.Agent) ~ enrolments =>
            authorisedAgentForMtdItId(mtdItId, enrolments) match {
              case Some(AgentDetails(mtdId, arn)) => block(User(mtdId, Some(arn))(request))
              case _ =>
                logger.warn("User did not have MTDID or ARN")
                unauthorized
            }
          case _ =>
            logger.info(s"[AuthorisedAction][async] - User failed to authenticate")
            unauthorized
        }.recover {
          case _ =>
            logger.info(s"[AuthorisedAction][async] - User failed to authenticate")
            Unauthorized
        }
    )
  }



}
