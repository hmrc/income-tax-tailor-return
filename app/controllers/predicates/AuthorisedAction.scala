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

import config.AppConfig
import models.{DelegatedAuthRules, User, Enrolment => EnrolmentKey}
import play.api.Logging
import play.api.mvc.Results.Unauthorized
import play.api.mvc._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.{affinityGroup, allEnrolments}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{Enrolment => HMRCEnrolment, _}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[User, AnyContent] with ActionFunction[Request, User]

class AuthorisedAction @Inject()(
                                  override val authConnector: AuthConnector,
                                  val parser: BodyParsers.Default,
                                  appConfig: AppConfig
                                )
                                (implicit val executionContext: ExecutionContext)
  extends IdentifierAction with AuthorisedFunctions with Logging {

  private val unauthorized: Future[Result] = Future.successful(Unauthorized)

  private def authorisedForMtdItId(enrolments: Enrolments): Option[String] =
    for {
      enrolment <- enrolments.getEnrolment(EnrolmentKey.Individual.key)
      id <- enrolment.getIdentifier(EnrolmentKey.Individual.value)
    } yield id.value

  private def getARN(enrolments: Enrolments): Option[String] =
    for {
      agentEnrolment <- enrolments.getEnrolment(EnrolmentKey.Agent.key)
      arn <- agentEnrolment.getIdentifier(EnrolmentKey.Agent.value)
    } yield arn.value

  private def predicate(mtdId: String): Predicate =
    HMRCEnrolment(EnrolmentKey.Individual.key)
      .withIdentifier(EnrolmentKey.Individual.value, mtdId)
      .withDelegatedAuthRule(DelegatedAuthRules.agentDelegatedAuthRule)

  private def secondaryAgentPredicate(mtdId: String): Predicate =
    HMRCEnrolment(EnrolmentKey.SupportingAgent.key)
      .withIdentifier(EnrolmentKey.SupportingAgent.value, mtdId)
      .withDelegatedAuthRule(DelegatedAuthRules.supportingAgentDelegatedAuthRule)

  override def invokeBlock[A](request: Request[A], block: User[A] => Future[Result]): Future[Result] = {

    implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    request.headers.get(EnrolmentKey.Individual.value).fold {
      logger.warn("[AuthorisedAction][async] - No MTDITID in the header. Returning unauthorised.")
      unauthorized
    }(
      mtdItId =>
        authorised().retrieve(affinityGroup and allEnrolments) {
          case Some(AffinityGroup.Individual) ~ enrolments if authorisedForMtdItId(enrolments).contains(mtdItId) =>
            block(User(mtdItId, None)(request))
          case Some(AffinityGroup.Agent) ~ enrolments =>
            getARN(enrolments) match {
              case Some(arn) =>
                authorised(predicate(mtdItId)) {
                  block(User(mtdItId, Some(arn))(request))
                }
              case _ =>
                logger.warn("[AuthorisedAction][async] - User did not have MTDID or ARN")
                unauthorized
            }
          case _ =>
            logger.info("[AuthorisedAction][async] - User failed to authenticate")
            unauthorized
        }.recover {
          case _ =>
            logger.info("[AuthorisedAction][async] - User failed to authenticate")
            Unauthorized
        }
    )
  }

  private def agentRecovery[A](block: User[A] => Future[Result],
                               mtdItId: String,
                               arn: String)(implicit request: Request[A], hc: HeaderCarrier): PartialFunction[Throwable, Future[Result]] = {
    case _: AuthorisationException if appConfig.emaSupportingAgentsEnabled =>
      authorised(secondaryAgentPredicate(mtdItId)) {
        block(User(mtdItId, Some(arn), isSecondaryAgent = true))
      } recoverWith { case _ =>
        logger.info(s"[AuthorisedAction][agentRecovery] - Agent does not have secondary delegated authority for Client.")
        unauthorized
      }
    case _ =>
      logger.info(s"[AuthorisedAction][agentRecovery] - Agent does not have delegated authority for Client.")
      unauthorized
  }
}

class EarlyPrivateLaunchAuthorisedAction @Inject()(
                                                    override val authConnector: AuthConnector,
                                                    val parser: BodyParsers.Default
                                                  )
                                                  (implicit val executionContext: ExecutionContext)
  extends IdentifierAction with AuthorisedFunctions with Logging {
  override def invokeBlock[A](request: Request[A], block: User[A] => Future[Result]): Future[Result] = {

    implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    authorised() {
      block(User("1234567890", None)(request))
    }
  }
}
