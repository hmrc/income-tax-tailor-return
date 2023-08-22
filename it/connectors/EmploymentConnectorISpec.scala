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

package connectors

import com.github.tomakehurst.wiremock.http.HttpHeader
import models.employment._
import models.{APIErrorBodyModel, APIErrorModel, APIErrorsBodyModel}
import play.api.http.Status._
import play.api.libs.json.Json
import support.ConnectorIntegrationTest
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, HttpResponse, SessionId}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class EmploymentConnectorISpec extends ConnectorIntegrationTest {

  private val nino = "AA123123A"
  private val taxYear = 2024

  private val mtditidHeader = ("mtditid", "123123123")
  private val requestHeaders = Seq(new HttpHeader("mtditid", "123123123"))

  private val underTest = new EmploymentConnector(httpClient, appConfigStub)

  val allEmploymentData: AllEmploymentData =
    AllEmploymentData(
      Seq(
        HmrcEmploymentSource(
          employmentId = "00000000-0000-0000-1111-000000000000",
          employerRef = Some("666/66666"),
          employerName = "Business",
          payrollId = Some("1234567890"),
          startDate = Some("2020-01-01"),
          cessationDate = Some("2020-01-01"),
          occupationalPension = Some(false),
          dateIgnored = Some("2020-01-01T10:00:38Z"),
          submittedOn = None,
          hmrcEmploymentFinancialData = Some(
            EmploymentFinancialData(
              employmentData = Some(EmploymentData(
                "2020-01-04T05:01:01Z",
                employmentSequenceNumber = Some("1002"),
                companyDirector = Some(false),
                closeCompany = Some(true),
                directorshipCeasedDate = Some("2020-02-12"),
                occPen = Some(false),
                disguisedRemuneration = Some(false),
                Some(Pay(
                  taxablePayToDate = Some(34234.15),
                  totalTaxToDate = Some(6782.92),
                  payFrequency = Some("CALENDAR MONTHLY"),
                  paymentDate = Some("2020-04-23"),
                  taxWeekNo = Some(32),
                  taxMonthNo = Some(2)
                )),
                Some(Deductions(
                  studentLoans = Some(StudentLoans(
                    uglDeductionAmount = Some(100),
                    pglDeductionAmount = Some(100)
                  ))
                ))
              )),
              employmentBenefits = Some(
                EmploymentBenefits(
                  "2020-01-04T05:01:01Z",
                  benefits = Some(Benefits(
                    Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100),
                    Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100),
                    Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100)
                  ))
                )
              )
            )
          ),
          customerEmploymentFinancialData = Some(
            EmploymentFinancialData(
              employmentData = Some(EmploymentData(
                "2020-01-04T05:01:01Z",
                employmentSequenceNumber = Some("1002"),
                companyDirector = Some(false),
                closeCompany = Some(true),
                directorshipCeasedDate = Some("2020-02-12"),
                occPen = Some(false),
                disguisedRemuneration = Some(false),
                Some(Pay(
                  taxablePayToDate = Some(34234.15),
                  totalTaxToDate = Some(6782.92),
                  payFrequency = Some("CALENDAR MONTHLY"),
                  paymentDate = Some("2020-04-23"),
                  taxWeekNo = Some(32),
                  taxMonthNo = Some(2)
                )),
                Some(Deductions(
                  studentLoans = Some(StudentLoans(
                    uglDeductionAmount = Some(100),
                    pglDeductionAmount = Some(100)
                  ))
                ))
              )),
              employmentBenefits = Some(
                EmploymentBenefits(
                  "2020-01-04T05:01:01Z",
                  benefits = Some(Benefits(
                    Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100),
                    Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100),
                    Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100)
                  ))
                )
              )
            )
          )
        )
      ),
      hmrcExpenses = Some(
        EmploymentExpenses(
          Some("2020-01-04T05:01:01Z"),
          Some("2020-01-04T05:01:01Z"),
          totalExpenses = Some(800),
          expenses = Some(Expenses(
            Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100)
          ))
        )
      ),
      Seq(
        EmploymentSource(
          employmentId = "00000000-0000-0000-2222-000000000000",
          employerRef = Some("666/66666"),
          employerName = "Business",
          payrollId = Some("1234567890"),
          startDate = Some("2020-01-01"),
          cessationDate = Some("2020-01-01"),
          occupationalPension = Some(false),
          dateIgnored = None,
          submittedOn = Some("2020-01-01T10:00:38Z"),
          employmentData = Some(
            EmploymentData(
              "2020-01-04T05:01:01Z",
              employmentSequenceNumber = Some("1002"),
              companyDirector = Some(false),
              closeCompany = Some(true),
              directorshipCeasedDate = Some("2020-02-12"),
              occPen = Some(false),
              disguisedRemuneration = Some(false),
              Some(Pay(
                taxablePayToDate = Some(34234.15),
                totalTaxToDate = Some(6782.92),
                payFrequency = Some("CALENDAR MONTHLY"),
                paymentDate = Some("2020-04-23"),
                taxWeekNo = Some(32),
                taxMonthNo = Some(2)
              )),
              Some(Deductions(
                studentLoans = Some(StudentLoans(
                  uglDeductionAmount = Some(100),
                  pglDeductionAmount = Some(100)
                ))
              ))
            )
          ),
          employmentBenefits = Some(
            EmploymentBenefits(
              "2020-01-04T05:01:01Z",
              benefits = Some(Benefits(
                Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100),
                Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100),
                Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100)
              ))
            )
          )
        )
      ),
      customerExpenses = Some(
        EmploymentExpenses(
          Some("2020-01-04T05:01:01Z"),
          Some("2020-01-04T05:01:01Z"),
          totalExpenses = Some(800),
          expenses = Some(Expenses(
            Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100), Some(100)
          ))
        )
      ),
      None
    )

  "IncomeTaxEmploymentConnector" should {
    "include internal headers" when {
      val expectedResult = Some(AllEmploymentData(Seq(HmrcEmploymentSource("id", "Employer Name", None, None, None, None, None, None, None, None, None)), None, Seq(), None, None))
      val responseBody = Json.toJson(expectedResult).toString()
      val parsedResult = Some(Set("Employer Name"))

      val headersSentToEmployment = Seq(new HttpHeader(HeaderNames.xSessionId, "sessionIdValue"))

      "the host for Employment is 'Internal'" in {
        implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("sessionIdValue")))

        stubGetHttpClientCall(
          s"/income-tax-employment/income-tax/nino/$nino/sources\\?taxYear=$taxYear",
          HttpResponse(OK, responseBody),
          headersSentToEmployment
        )

        Await.result(underTest.getSubmittedEmployment(nino, taxYear), Duration.Inf) shouldBe Right(parsedResult)
      }
    }

    "return a Set of employer names" when {
      "all values are present" in {
        val requestBody = Json.toJson(Some(allEmploymentData))
        implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(mtditidHeader)

        stubGetHttpClientCall(
          s"/income-tax-employment/income-tax/nino/$nino/sources\\?taxYear=$taxYear",
          HttpResponse(OK, requestBody.toString()),
          requestHeaders
        )

        Await.result(underTest.getSubmittedEmployment(nino, taxYear), Duration.Inf) shouldBe Right(Some(Set("Business")))
      }
    }


    "return a none when no employment info found" in {
      val response = Json.toJson(AllEmploymentData(Seq(), None, Seq(), None, None)).toString()

      stubGetHttpClientCall(
        s"/income-tax-employment/income-tax/nino/$nino/sources\\?taxYear=$taxYear",
        HttpResponse(OK, response),
        requestHeaders
      )

      implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(mtditidHeader)

      Await.result(underTest.getSubmittedEmployment(nino, taxYear), Duration.Inf) shouldBe Right(None)
    }

    "return a None for no content" in {
      stubGetHttpClientCall(
        s"/income-tax-employment/income-tax/nino/$nino/sources\\?taxYear=$taxYear",
        HttpResponse(NO_CONTENT, "{}"),
        requestHeaders
      )

      implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(mtditidHeader)

      Await.result(underTest.getSubmittedEmployment(nino, taxYear), Duration.Inf) shouldBe Right(None)
    }

    "API Returns multiple errors" in {
      val expectedResult = APIErrorModel(BAD_REQUEST, APIErrorsBodyModel(Seq(
        APIErrorBodyModel("INVALID_IDTYPE", "ID is invalid"),
        APIErrorBodyModel("INVALID_IDTYPE_2", "ID 2 is invalid"))
      ))

      val response = Json.obj("failures" -> Json.arr(
        Json.obj("code" -> "INVALID_IDTYPE", "reason" -> "ID is invalid"),
        Json.obj("code" -> "INVALID_IDTYPE_2", "reason" -> "ID 2 is invalid")
      ))

      stubGetHttpClientCall(
        s"/income-tax-employment/income-tax/nino/$nino/sources\\?taxYear=$taxYear",
        HttpResponse(BAD_REQUEST, response.toString()),
        requestHeaders
      )

      implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(mtditidHeader)

      Await.result(underTest.getSubmittedEmployment(nino, taxYear), Duration.Inf) shouldBe Left(expectedResult)
    }


    "return a BadRequest" in {
      val errorBody: APIErrorBodyModel = APIErrorBodyModel("BAD_REQUEST", "That request was bad")
      val expectedResult = APIErrorModel(BAD_REQUEST, errorBody)

      stubGetHttpClientCall(
        s"/income-tax-employment/income-tax/nino/$nino/sources\\?taxYear=$taxYear",
        HttpResponse(BAD_REQUEST, Json.toJson(errorBody).toString()),
        requestHeaders
      )

      implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(mtditidHeader)

      Await.result(underTest.getSubmittedEmployment(nino, taxYear), Duration.Inf) shouldBe Left(expectedResult)
    }

    "return an InternalServerError " in {
      val errorBody: APIErrorBodyModel = APIErrorBodyModel("INTERNAL_SERVER_ERROR", "Something went wrong")
      val expectedResult = APIErrorModel(INTERNAL_SERVER_ERROR, errorBody)

      stubGetHttpClientCall(
        s"/income-tax-employment/income-tax/nino/$nino/sources\\?taxYear=$taxYear",
        HttpResponse(INTERNAL_SERVER_ERROR, Json.toJson(errorBody).toString()),
        requestHeaders
      )

      implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(mtditidHeader)

      Await.result(underTest.getSubmittedEmployment(nino, taxYear), Duration.Inf) shouldBe Left(expectedResult)
    }

    "return an InternalServerError due to parsing error" in {
      val invalidJson = Json.obj("employment" -> "")
      val expectedResult = APIErrorModel(INTERNAL_SERVER_ERROR, APIErrorBodyModel.parsingError)

      stubGetHttpClientCall(
        s"/income-tax-employment/income-tax/nino/$nino/sources\\?taxYear=$taxYear",
        HttpResponse(OK, invalidJson.toString()),
        requestHeaders
      )

      implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(mtditidHeader)

      Await.result(underTest.getSubmittedEmployment(nino, taxYear), Duration.Inf) shouldBe Left(expectedResult)
    }

    "return an InternalServerError with parsing error when we can't parse the error body" in {
      val errorBody = "INTERNAL_SERVER_ERROR"
      val expectedResult = APIErrorModel(INTERNAL_SERVER_ERROR, APIErrorBodyModel.parsingError)

      stubGetHttpClientCall(
        s"/income-tax-employment/income-tax/nino/$nino/sources\\?taxYear=$taxYear",
        HttpResponse(INTERNAL_SERVER_ERROR, Json.toJson(errorBody).toString()),
        requestHeaders
      )

      implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(mtditidHeader)

      Await.result(underTest.getSubmittedEmployment(nino, taxYear), Duration.Inf) shouldBe Left(expectedResult)
    }

    "return an InternalServerError when an unexpected status is thrown" in {
      val errorBody: APIErrorBodyModel = APIErrorBodyModel("INTERNAL_SERVER_ERROR", "Something went wrong")
      val expectedResult = APIErrorModel(INTERNAL_SERVER_ERROR, errorBody)

      stubGetHttpClientCall(
        s"/income-tax-employment/income-tax/nino/$nino/sources\\?taxYear=$taxYear",
        HttpResponse(IM_A_TEAPOT, Json.toJson(errorBody).toString()),
        requestHeaders
      )

      implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(mtditidHeader)

      Await.result(underTest.getSubmittedEmployment(nino, taxYear), Duration.Inf) shouldBe Left(expectedResult)
    }

    "return an InternalServerError when an unexpected status is thrown and there is no body" in {
      val expectedResult = APIErrorModel(INTERNAL_SERVER_ERROR, APIErrorBodyModel.parsingError)

      stubGetHttpClientCall(
        s"/income-tax-employment/income-tax/nino/$nino/sources\\?taxYear=$taxYear",
        HttpResponse(IM_A_TEAPOT, ""),
        requestHeaders
      )

      implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(mtditidHeader)

      Await.result(underTest.getSubmittedEmployment(nino, taxYear), Duration.Inf) shouldBe Left(expectedResult)
    }

    "return a ServiceUnavailableError" in {
      val errorBody: APIErrorBodyModel = APIErrorBodyModel("SERVICE_UNAVAILABLE", "Service went down")
      val expectedResult = APIErrorModel(SERVICE_UNAVAILABLE, errorBody)

      stubGetHttpClientCall(
        s"/income-tax-employment/income-tax/nino/$nino/sources\\?taxYear=$taxYear",
        HttpResponse(SERVICE_UNAVAILABLE, Json.toJson(errorBody).toString()),
        requestHeaders
      )

      implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(mtditidHeader)

      Await.result(underTest.getSubmittedEmployment(nino, taxYear), Duration.Inf) shouldBe Left(expectedResult)
    }
  }
}

