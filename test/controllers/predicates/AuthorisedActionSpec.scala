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

import com.google.inject.Inject
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import play.api.inject.bind
import org.mockito.ArgumentMatchers.{any, eq => mEq}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import uk.gov.hmrc.auth.core.{Enrolment => HMRCEnrolment}

class AuthorisedActionSpec extends AnyWordSpec with Matchers {

  class Harness(authAction: IdentifierAction) {
    def onPageLoad(): Action[AnyContent] = authAction { _ => Results.Ok }
  }
  
  val mtdEnrollmentKey = "HMRC-MTD-IT"
  val mtdEnrollmentIdentifier = "MTDITID"
  private val mockAuthConnector: AuthConnector = Mockito.mock(classOf[AuthConnector])
  private type RetrievalType = Option[AffinityGroup] ~ Enrolments

  "Auth Action" should {
    val app = new GuiceApplicationBuilder().build()
    val bodyParsers = app.injector.instanceOf[BodyParsers.Default]
    "succeed with a identifier Request" when {

      "the user is authorised as an individual" in {

        running(app) {

          val enrolments: Enrolments = Enrolments(Set(
            Enrolment(mtdEnrollmentKey, Seq(EnrolmentIdentifier(mtdEnrollmentIdentifier, "1234567890")), "Activated")
          ))

          val AuthResponse: Some[AffinityGroup] ~ Enrolments =
            new ~(
              Some(AffinityGroup.Individual),
              enrolments)

          val authAction = new AuthorisedAction(new FakeSuccessfulAuthConnector(AuthResponse), bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest().withHeaders("mtditid" -> "1234567890"))

          status(result) shouldBe OK
        }
      }
      "the user is authorised as an agent" in {
        val app = new GuiceApplicationBuilder().overrides(bind[AuthConnector].toInstance(mockAuthConnector)).build()

        val bodyParsers = app.injector.instanceOf[BodyParsers.Default]

        def predicate(mtdId: String): Predicate = mEq(
          HMRCEnrolment("HMRC-MTD-IT")
            .withIdentifier("MTDITID", mtdId)
            .withDelegatedAuthRule("mtd-it-auth"))

        running(app) {

          val enrolments: Enrolments = Enrolments(Set(
            Enrolment(models.Enrolment.Agent.key, Seq(EnrolmentIdentifier(models.Enrolment.Agent.value, "XARN1234567")), "Activated")
          ))

          val authResponse = Future.successful(
            new ~(Some(AffinityGroup.Agent), enrolments)
          )

          when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]])(any(), any()))
            .thenReturn(authResponse)

          when(mockAuthConnector.authorise(predicate("1234567890"), any[Retrieval[Unit]])(any(), any()))
            .thenReturn(Future.successful(()))

          val authAction = new AuthorisedAction(mockAuthConnector, bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest().withHeaders("mtditid" -> "1234567890"))

          status(result) shouldBe OK
        }
      }
    }
    "return with a Unauthorized for an individual" when {

      "the mtditid in the user's header does not exist" in {

        running(app) {

          val enrolments: Enrolments = Enrolments(Set(
            Enrolment(mtdEnrollmentKey, Seq(EnrolmentIdentifier(mtdEnrollmentIdentifier, "1234567890")), "Activated")
          ))

          val AuthResponse: Some[AffinityGroup] ~ Enrolments =
            new ~(
              Some(AffinityGroup.Individual),
              enrolments)

          val authAction = new AuthorisedAction(new FakeSuccessfulAuthConnector(AuthResponse), bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest())

          status(result) shouldBe UNAUTHORIZED
        }
      }
      "the mtditid in the users header does not match the mtditid within their enrollments" in {

        running(app) {

          val enrolments: Enrolments = Enrolments(Set(
            Enrolment(mtdEnrollmentKey, Seq(EnrolmentIdentifier(mtdEnrollmentIdentifier, "1234567890")), "Activated")
          ))

          val AuthResponse: Some[AffinityGroup] ~ Enrolments =
            new ~(
              Some(AffinityGroup.Individual),
              enrolments)

          val authAction = new AuthorisedAction(new FakeSuccessfulAuthConnector(AuthResponse), bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest().withHeaders("mtditid" -> "we are not the same"))

          status(result) shouldBe UNAUTHORIZED
        }
      }
    }
    "return with a Unauthorized for an agent" when {
      "an agent does not have a ARN in their enrollments" in {

        running(app) {

          val enrolments: Enrolments = Enrolments(Set(
            Enrolment(mtdEnrollmentKey, Seq(EnrolmentIdentifier(mtdEnrollmentIdentifier, "1234567890")), "Activated")
          ))

          val AuthResponse: Some[AffinityGroup] ~ Enrolments =
            new ~(
              Some(AffinityGroup.Agent),
              enrolments)

          val authAction = new AuthorisedAction(new FakeSuccessfulAuthConnector(AuthResponse), bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest().withHeaders("mtditid" -> "1234567890"))

          status(result) shouldBe UNAUTHORIZED
        }
      }
      "an agent does not have a matching mtditid" in {

        running(app) {

          val enrolments: Enrolments = Enrolments(Set(
            Enrolment(mtdEnrollmentKey, Seq(EnrolmentIdentifier(mtdEnrollmentIdentifier, "7777777777")), "Activated"),
            Enrolment(mtdEnrollmentKey, Seq(EnrolmentIdentifier(mtdEnrollmentIdentifier, "8888888888")), "Activated"),
            Enrolment(mtdEnrollmentKey, Seq(EnrolmentIdentifier(mtdEnrollmentIdentifier, "1234567890")), "Activated")
          ) + Enrolment(models.Enrolment.Agent.key, Seq(EnrolmentIdentifier(models.Enrolment.Agent.value, "XARN1234567")), "Activated"))

          val AuthResponse: Some[AffinityGroup] ~ Enrolments =
            new ~(
              Some(AffinityGroup.Agent),
              enrolments)

          val authAction = new AuthorisedAction(new FakeSuccessfulAuthConnector(AuthResponse), bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest().withHeaders("mtditid" -> "we do not exist"))

          status(result) shouldBe UNAUTHORIZED
        }
      }
      "authConnector fails" in {

        running(app) {

          val authAction = new AuthorisedAction(new FakeFailingAuthConnector(new MissingBearerToken), bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest().withHeaders("mtditid" -> "1234567890"))

          status(result) shouldBe UNAUTHORIZED
        }
      }
    }
  }
  "Auth Action [EarlyPrivateLaunchAuthorisedAction]" should {

    "succeed with a identifier Request" in {

      val application = new GuiceApplicationBuilder()
        .configure(Map("features.earlyPrivateLaunch" -> "true"))
        .build()

      running(application) {

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new EarlyPrivateLaunchAuthorisedAction(new FakeSuccessfulAuthConnector(()), bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(FakeRequest())

        status(result) shouldBe OK
      }
    }

  }
}

class FakeFailingAuthConnector @Inject()(exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
class FakeSuccessfulAuthConnector[T] @Inject()(value: T) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.fromTry(Try(value.asInstanceOf[A]))
}
