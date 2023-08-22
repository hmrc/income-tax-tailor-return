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

import com.codahale.metrics.SharedMetricRegistries
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.{JsObject, Json}
import support.UnitTest

class AllEmploymentDataModelSpec extends UnitTest {
  SharedMetricRegistries.clear()

  val jsonModel: JsObject = Json.obj(
      "hmrcEmploymentData" -> Json.arr(
        Json.obj(
          "employmentId" -> "00000000-0000-0000-1111-000000000000",
          "employerName" -> "Business",
          "employerRef" -> "666/66666",
          "payrollId" -> "1234567890",
          "startDate" -> "2020-01-01",
          "cessationDate" -> "2020-01-01",
          "occupationalPension" -> false,
          "dateIgnored" -> "2020-01-01T10:00:38Z",
          "hmrcEmploymentFinancialData" -> Json.obj(
          "employmentData" -> Json.obj(
            "submittedOn" -> "2020-01-04T05:01:01Z",
            "employmentSequenceNumber" -> "1002",
            "companyDirector" -> false,
            "closeCompany" -> true,
            "directorshipCeasedDate" -> "2020-02-12",
            "occPen" -> false,
            "disguisedRemuneration" -> false,
            "pay" -> Json.obj(
              "taxablePayToDate" -> 34234.15,
              "totalTaxToDate" -> 6782.92,
              "payFrequency" -> "CALENDAR MONTHLY",
              "paymentDate" -> "2020-04-23",
              "taxWeekNo" -> 32,
              "taxMonthNo" -> 2
            ),
            "deductions" -> Json.obj(
              "studentLoans" -> Json.obj(
                "uglDeductionAmount" -> 100,
                "pglDeductionAmount" -> 100
              )
            )
          ),
          "employmentBenefits" -> Json.obj(
            "submittedOn" -> "2020-01-04T05:01:01Z",
            "benefits" -> Json.obj(
              "accommodation" -> 100,
              "assets" -> 100,
              "assetTransfer" -> 100,
              "beneficialLoan" -> 100,
              "car" -> 100,
              "carFuel" -> 100,
              "educationalServices" -> 100,
              "entertaining" -> 100,
              "expenses" -> 100,
              "medicalInsurance" -> 100,
              "telephone" -> 100,
              "service" -> 100,
              "taxableExpenses" -> 100,
              "van" -> 100,
              "vanFuel" -> 100,
              "mileage" -> 100,
              "nonQualifyingRelocationExpenses" -> 100,
              "nurseryPlaces" -> 100,
              "otherItems" -> 100,
              "paymentsOnEmployeesBehalf" -> 100,
              "personalIncidentalExpenses" -> 100,
              "qualifyingRelocationExpenses" -> 100,
              "employerProvidedProfessionalSubscriptions" -> 100,
              "employerProvidedServices" -> 100,
              "incomeTaxPaidByDirector" -> 100,
              "travelAndSubsistence" -> 100,
              "vouchersAndCreditCards" -> 100,
              "nonCash" -> 100
            )
          )
          ),
          "customerEmploymentFinancialData" -> Json.obj(
          "employmentData" -> Json.obj(
            "submittedOn" -> "2020-01-04T05:01:01Z",
            "employmentSequenceNumber" -> "1002",
            "companyDirector" -> false,
            "closeCompany" -> true,
            "directorshipCeasedDate" -> "2020-02-12",
            "occPen" -> false,
            "disguisedRemuneration" -> false,
            "pay" -> Json.obj(
              "taxablePayToDate" -> 34234.15,
              "totalTaxToDate" -> 6782.92,
              "payFrequency" -> "CALENDAR MONTHLY",
              "paymentDate" -> "2020-04-23",
              "taxWeekNo" -> 32,
              "taxMonthNo" -> 2
            ),
            "deductions" -> Json.obj(
              "studentLoans" -> Json.obj(
                "uglDeductionAmount" -> 100,
                "pglDeductionAmount" -> 100
              )
            )
          ),
          "employmentBenefits" -> Json.obj(
            "submittedOn" -> "2020-01-04T05:01:01Z",
            "benefits" -> Json.obj(
              "accommodation" -> 100,
              "assets" -> 100,
              "assetTransfer" -> 100,
              "beneficialLoan" -> 100,
              "car" -> 100,
              "carFuel" -> 100,
              "educationalServices" -> 100,
              "entertaining" -> 100,
              "expenses" -> 100,
              "medicalInsurance" -> 100,
              "telephone" -> 100,
              "service" -> 100,
              "taxableExpenses" -> 100,
              "van" -> 100,
              "vanFuel" -> 100,
              "mileage" -> 100,
              "nonQualifyingRelocationExpenses" -> 100,
              "nurseryPlaces" -> 100,
              "otherItems" -> 100,
              "paymentsOnEmployeesBehalf" -> 100,
              "personalIncidentalExpenses" -> 100,
              "qualifyingRelocationExpenses" -> 100,
              "employerProvidedProfessionalSubscriptions" -> 100,
              "employerProvidedServices" -> 100,
              "incomeTaxPaidByDirector" -> 100,
              "travelAndSubsistence" -> 100,
              "vouchersAndCreditCards" -> 100,
              "nonCash" -> 100
            )
          )
          )
        )),
    "hmrcExpenses" -> Json.obj(
      "submittedOn" -> "2020-01-04T05:01:01Z",
      "dateIgnored" -> "2020-01-04T05:01:01Z",
      "totalExpenses" -> 800,
      "expenses" -> Json.obj(
        "businessTravelCosts" -> 100,
        "jobExpenses" -> 100,
        "flatRateJobExpenses" -> 100,
        "professionalSubscriptions" -> 100,
        "hotelAndMealExpenses" -> 100,
        "otherAndCapitalAllowances" -> 100,
        "vehicleExpenses" -> 100,
        "mileageAllowanceRelief" -> 100
      )
    ),
      "customerEmploymentData" -> Json.arr(
        Json.obj(
          "employmentId" -> "00000000-0000-0000-2222-000000000000",
          "employerName" -> "Business",
          "employerRef" -> "666/66666",
          "payrollId" -> "1234567890",
          "startDate" -> "2020-01-01",
          "cessationDate" -> "2020-01-01",
          "occupationalPension" -> false,
          "submittedOn" -> "2020-01-01T10:00:38Z",
          "employmentData" -> Json.obj(
            "submittedOn" -> "2020-01-04T05:01:01Z",
            "employmentSequenceNumber" -> "1002",
            "companyDirector" -> false,
            "closeCompany" -> true,
            "directorshipCeasedDate" -> "2020-02-12",
            "occPen" -> false,
            "disguisedRemuneration" -> false,
            "pay" -> Json.obj(
              "taxablePayToDate" -> 34234.15,
              "totalTaxToDate" -> 6782.92,
              "payFrequency" -> "CALENDAR MONTHLY",
              "paymentDate" -> "2020-04-23",
              "taxWeekNo" -> 32,
              "taxMonthNo" -> 2
            ),
            "deductions" -> Json.obj(
              "studentLoans" -> Json.obj(
                "uglDeductionAmount" -> 100,
                "pglDeductionAmount" -> 100
              )
            )
          ),
          "employmentBenefits" -> Json.obj(
            "submittedOn" -> "2020-01-04T05:01:01Z",
            "benefits" -> Json.obj(
              "accommodation" -> 100,
              "assets" -> 100,
              "assetTransfer" -> 100,
              "beneficialLoan" -> 100,
              "car" -> 100,
              "carFuel" -> 100,
              "educationalServices" -> 100,
              "entertaining" -> 100,
              "expenses" -> 100,
              "medicalInsurance" -> 100,
              "telephone" -> 100,
              "service" -> 100,
              "taxableExpenses" -> 100,
              "van" -> 100,
              "vanFuel" -> 100,
              "mileage" -> 100,
              "nonQualifyingRelocationExpenses" -> 100,
              "nurseryPlaces" -> 100,
              "otherItems" -> 100,
              "paymentsOnEmployeesBehalf" -> 100,
              "personalIncidentalExpenses" -> 100,
              "qualifyingRelocationExpenses" -> 100,
              "employerProvidedProfessionalSubscriptions" -> 100,
              "employerProvidedServices" -> 100,
              "incomeTaxPaidByDirector" -> 100,
              "travelAndSubsistence" -> 100,
              "vouchersAndCreditCards" -> 100,
              "nonCash" -> 100
            )
          )
        )),
    "customerExpenses" -> Json.obj(
      "submittedOn" -> "2020-01-04T05:01:01Z",
      "dateIgnored" -> "2020-01-04T05:01:01Z",
      "totalExpenses" -> 800,
      "expenses" -> Json.obj(
        "businessTravelCosts" -> 100,
        "jobExpenses" -> 100,
        "flatRateJobExpenses" -> 100,
        "professionalSubscriptions" -> 100,
        "hotelAndMealExpenses" -> 100,
        "otherAndCapitalAllowances" -> 100,
        "vehicleExpenses" -> 100,
        "mileageAllowanceRelief" -> 100
      )
    )
  )

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

  "AllEmploymentData" should {

    "parse to Json" in {
      Json.toJson(allEmploymentData) mustBe jsonModel
    }

    "parse from Json" in {
      jsonModel.as[AllEmploymentData]
    }

    "get employer names" in {
      allEmploymentData.getEmployerNames mustBe Set("Business")
    }
  }

}
