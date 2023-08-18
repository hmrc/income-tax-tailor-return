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

package models.employment

import play.api.libs.json.{Json, OFormat}

case class AllEmploymentData(hmrcEmploymentData: Seq[HmrcEmploymentSource],
                             hmrcExpenses: Option[EmploymentExpenses],
                             customerEmploymentData: Seq[EmploymentSource],
                             customerExpenses: Option[EmploymentExpenses],
                             otherEmploymentIncome: Option[OtherEmploymentIncome]) {
  def getEmployerNames: Set[String] = {
    (hmrcEmploymentData.map(_.employerName) ++ customerEmploymentData.map(_.employerName)).toSet
  }

}

object AllEmploymentData {
  implicit val format: OFormat[AllEmploymentData] = Json.format[AllEmploymentData]
}