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

import connectors.parsers.APIParser
import models.{APIErrorBodyModel, APIErrorModel, APIErrorsBodyModel}
import org.scalamock.matchers.Matchers
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json.{JsValue, Json}
import support.UnitTest
import uk.gov.hmrc.http.HttpResponse

class APIParserSpec extends UnitTest with Matchers {

  object FakeParser extends APIParser {
    override val parserName: String = "APIParser"
    override val service: String = "fake-service"
  }

  def httpResponse(json: JsValue =
                   Json.parse(
                     """{"failures":[
                       |{"code":"SERVICE_UNAVAILABLE","reason":"The service is currently unavailable"},
                       |{"code":"INTERNAL_SERVER_ERROR","reason":"The service is currently facing issues."}]}""".stripMargin)): HttpResponse = HttpResponse(
    INTERNAL_SERVER_ERROR,
    json,
    Map("CorrelationId" -> Seq("1234645654645"))
  )

  "FakeParser" should {
    "log the correct message" in {
      val result = FakeParser.logMessage(httpResponse())
      result mustBe (
        s"""[APIParser][read] Received 500 from ${FakeParser.service} API. Body:{
           |  "failures" : [ {
           |    "code" : "SERVICE_UNAVAILABLE",
           |    "reason" : "The service is currently unavailable"
           |  }, {
           |    "code" : "INTERNAL_SERVER_ERROR",
           |    "reason" : "The service is currently facing issues."
           |  } ]
           |} CorrelationId: 1234645654645""".stripMargin)
    }
    "return the the correct error" in {
      val result = FakeParser.badSuccessJsonFromAPI
      result mustBe Left(APIErrorModel(INTERNAL_SERVER_ERROR, APIErrorBodyModel("PARSING_ERROR", "Error parsing response from API")))
    }
    "handle multiple errors" in {
      val result = FakeParser.handleAPIError(httpResponse())
      result mustBe Left(APIErrorModel(INTERNAL_SERVER_ERROR, APIErrorsBodyModel(Seq(
        APIErrorBodyModel("SERVICE_UNAVAILABLE", "The service is currently unavailable"),
        APIErrorBodyModel("INTERNAL_SERVER_ERROR", "The service is currently facing issues.")
      ))))
    }
    "handle single errors" in {
      val result = FakeParser.handleAPIError(httpResponse(Json.parse(
        """{"code":"INTERNAL_SERVER_ERROR","reason":"The service is currently facing issues."}""".stripMargin)))
      result mustBe Left(APIErrorModel(INTERNAL_SERVER_ERROR, APIErrorBodyModel("INTERNAL_SERVER_ERROR", "The service is currently facing issues.")))
    }
  }

}