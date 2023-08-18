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

import connectors.EmploymentConnector
import models.{APIErrorBodyModel, APIErrorModel}
import org.scalamock.handlers.CallHandler3
import play.api.http.Status.INTERNAL_SERVER_ERROR
import support.ControllerUnitTest
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class EmploymentServiceSpec extends ControllerUnitTest {

  val mockConnector = mock[EmploymentConnector]
  val underTest: EmploymentService = new EmploymentService(mockConnector)

  private val nino = "A123459A"
  private val taxYear = 2023

  def mockGetEmploymentData(
                               nino: String,
                               taxYear: Int,
                               result: Either[APIErrorModel, Option[Set[String]]]
                             ): CallHandler3[String, Int, HeaderCarrier, Future[Either[APIErrorModel, Option[Set[String]]]]] = {
    (mockConnector.getSubmittedEmployment(_: String, _: Int)(_: HeaderCarrier))
      .expects(nino, taxYear, *)
      .returning(Future.successful(result))
  }

  ".getEmploymentData" should {

    "return a list of employers" in {

      mockGetEmploymentData(nino, taxYear, Right(Some(Set[String]("Employer Name", "Another Employer"))))

      val result = await(underTest.getEmploymentData(nino, taxYear))

      result shouldBe Right(Some(Set[String]("Employer Name", "Another Employer")))
    }

    "return an error" in {

      mockGetEmploymentData(nino, taxYear, Left(APIErrorModel(INTERNAL_SERVER_ERROR, APIErrorBodyModel("INTERNAL_SERVER_ERROR", "Reason"))))

      val result = await(underTest.getEmploymentData(nino, taxYear))

      result shouldBe Left(APIErrorModel(INTERNAL_SERVER_ERROR, APIErrorBodyModel("INTERNAL_SERVER_ERROR", "Reason")))
    }
  }
}
