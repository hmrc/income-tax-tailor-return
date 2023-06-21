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

package models.tailoring

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.EncryptedValue
import utils.AesGcmAdCrypto
import utils.CypherSyntax.{DecryptableOps, EncryptableOps}

case class AllowancesTaxReliefModel(
                                     blindPersonAllowance: Option[Boolean] = None,
                                     marriedCoupleAllowance: Option[Boolean] = None,
                                     ventureCapitalSchemes: Option[Boolean] = None,
                                     enterpriseInvestmentScheme: Option[Boolean] = None,
                                     seedEnterpriseInvestmentScheme: Option[Boolean] = None,
                                     socialInvestmentTaxRelief: Option[Boolean] = None,
                                     ventureCapitalTrust: Option[Boolean] = None,
                                     nonDeductibleLoanInterest : Option[Boolean] = None,
                                     tradeUnionDeath: Option[Boolean] = None,
                                     qualifyingLoanInterest: Option[Boolean] = None,
                                     qualifyingDistributionRelief: Option[Boolean] = None,
                                     seafarersEarningsDeduction: Option[Boolean] = None
                                ) {
  def encrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): EncryptedAllowancesTaxReliefModel =
    EncryptedAllowancesTaxReliefModel(
      blindPersonAllowance.map(_.encrypted),
      marriedCoupleAllowance = marriedCoupleAllowance.map(_.encrypted),
      ventureCapitalSchemes = ventureCapitalSchemes.map(_.encrypted),
      enterpriseInvestmentScheme = enterpriseInvestmentScheme.map(_.encrypted),
      seedEnterpriseInvestmentScheme = seedEnterpriseInvestmentScheme.map(_.encrypted),
      socialInvestmentTaxRelief = socialInvestmentTaxRelief.map(_.encrypted),
      ventureCapitalTrust = ventureCapitalTrust.map(_.encrypted),
      nonDeductibleLoanInterest = nonDeductibleLoanInterest.map(_.encrypted),
      tradeUnionDeath = tradeUnionDeath.map(_.encrypted),
      qualifyingLoanInterest = qualifyingLoanInterest.map(_.encrypted),
      qualifyingDistributionRelief = qualifyingDistributionRelief.map(_.encrypted),
      seafarersEarningsDeduction = seafarersEarningsDeduction.map(_.encrypted)
    )
}

object AllowancesTaxReliefModel {
  implicit lazy val formats: OFormat[AllowancesTaxReliefModel] = Json.format[AllowancesTaxReliefModel]
}

case class EncryptedAllowancesTaxReliefModel(
                                              blindPersonAllowance: Option[EncryptedValue] = None,
                                              marriedCoupleAllowance: Option[EncryptedValue] = None,
                                              ventureCapitalSchemes: Option[EncryptedValue] = None,
                                              enterpriseInvestmentScheme: Option[EncryptedValue] = None,
                                              seedEnterpriseInvestmentScheme: Option[EncryptedValue] = None,
                                              socialInvestmentTaxRelief: Option[EncryptedValue] = None,
                                              ventureCapitalTrust: Option[EncryptedValue] = None,
                                              nonDeductibleLoanInterest: Option[EncryptedValue] = None,
                                              tradeUnionDeath: Option[EncryptedValue] = None,
                                              qualifyingLoanInterest: Option[EncryptedValue] = None,
                                              qualifyingDistributionRelief: Option[EncryptedValue] = None,
                                              seafarersEarningsDeduction: Option[EncryptedValue] = None
                                ) {
  def decrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): AllowancesTaxReliefModel =
    AllowancesTaxReliefModel(
      blindPersonAllowance = blindPersonAllowance.map(_.decrypted[Boolean]),
      marriedCoupleAllowance = marriedCoupleAllowance.map(_.decrypted[Boolean]),
      ventureCapitalSchemes = ventureCapitalSchemes.map(_.decrypted[Boolean]),
      enterpriseInvestmentScheme = enterpriseInvestmentScheme.map(_.decrypted[Boolean]),
      seedEnterpriseInvestmentScheme = seedEnterpriseInvestmentScheme.map(_.decrypted[Boolean]),
      socialInvestmentTaxRelief = socialInvestmentTaxRelief.map(_.decrypted[Boolean]),
      ventureCapitalTrust = ventureCapitalTrust.map(_.decrypted[Boolean]),
      nonDeductibleLoanInterest = nonDeductibleLoanInterest.map(_.decrypted[Boolean]),
      tradeUnionDeath = tradeUnionDeath.map(_.decrypted[Boolean]),
      qualifyingLoanInterest = qualifyingLoanInterest.map(_.decrypted[Boolean]),
      qualifyingDistributionRelief = qualifyingDistributionRelief.map(_.decrypted[Boolean]),
      seafarersEarningsDeduction = seafarersEarningsDeduction.map(_.decrypted[Boolean])
    )

}

object EncryptedAllowancesTaxReliefModel {
  implicit lazy val encryptedValueOFormat: OFormat[EncryptedValue] = Json.format[EncryptedValue]
  implicit lazy val formats: OFormat[EncryptedAllowancesTaxReliefModel] = Json.format[EncryptedAllowancesTaxReliefModel]
}
