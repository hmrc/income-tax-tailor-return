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




case class WorkBenefitsModel(
                              employed: Option[Boolean] = None,
                              partnership: Option[Boolean] = None,
                              lloydUnderwriter: Option[Boolean] = None,
                              ministerOfReligion: Option[Boolean] = None,
                              memberOfParliament: Option[Boolean] = None,
                              lumpSums: Option[Boolean] = None,
                              untaxedIncomeFromShareSchemes: Option[Boolean] = None,
                              haveCisDeductions: Option[Boolean] = None,
                              JobseekersAllowance: Option[Boolean] = None,
                              EmploymentSupportAllowance: Option[Boolean] = None,
                              taxableStateBenefit: Option[Boolean] = None,
                              taxRefundOrOffset: Option[Boolean] = None

                            ) {
  def encrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): EncryptedWorkBenefitsModel =
    EncryptedWorkBenefitsModel(
      employed = employed.map(_.encrypted),
      partnership = partnership.map(_.encrypted),
      lloydUnderwriter = lloydUnderwriter.map(_.encrypted),
      ministerOfReligion = ministerOfReligion.map(_.encrypted),
      memberOfParliament = memberOfParliament.map(_.encrypted),
      lumpSums = lumpSums.map(_.encrypted),
      untaxedIncomeFromShareSchemes = untaxedIncomeFromShareSchemes.map(_.encrypted),
      haveCisDeductions = haveCisDeductions.map(_.encrypted),
      JobseekersAllowance = JobseekersAllowance.map(_.encrypted),
      EmploymentSupportAllowance = EmploymentSupportAllowance.map(_.encrypted),
      taxableStateBenefit = taxableStateBenefit.map(_.encrypted),
      taxRefundOrOffset = taxRefundOrOffset.map(_.encrypted)
    )
}

object WorkBenefitsModel {
  implicit lazy val formats: OFormat[WorkBenefitsModel] = Json.format[WorkBenefitsModel]
}
case class EncryptedWorkBenefitsModel(
                              employed: Option[EncryptedValue] = None,
                              partnership: Option[EncryptedValue] = None,
                              lloydUnderwriter: Option[EncryptedValue] = None,
                              ministerOfReligion: Option[EncryptedValue] = None,
                              memberOfParliament: Option[EncryptedValue] = None,
                              lumpSums: Option[EncryptedValue] = None,
                              untaxedIncomeFromShareSchemes: Option[EncryptedValue] = None,
                              haveCisDeductions: Option[EncryptedValue] = None,
                              JobseekersAllowance: Option[EncryptedValue] = None,
                              EmploymentSupportAllowance: Option[EncryptedValue] = None,
                              taxableStateBenefit: Option[EncryptedValue] = None,
                              taxRefundOrOffset: Option[EncryptedValue] = None

                            ) {
  def decrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): WorkBenefitsModel =
    WorkBenefitsModel(
      employed = employed.map(_.decrypted[Boolean]),
      partnership = partnership.map(_.decrypted[Boolean]),
      lloydUnderwriter = lloydUnderwriter.map(_.decrypted[Boolean]),
      ministerOfReligion = ministerOfReligion.map(_.decrypted[Boolean]),
      memberOfParliament = memberOfParliament.map(_.decrypted[Boolean]),
      lumpSums = lumpSums.map(_.decrypted[Boolean]),
      untaxedIncomeFromShareSchemes = untaxedIncomeFromShareSchemes.map(_.decrypted[Boolean]),
      haveCisDeductions = haveCisDeductions.map(_.decrypted[Boolean]),
      JobseekersAllowance = JobseekersAllowance.map(_.decrypted[Boolean]),
      EmploymentSupportAllowance = EmploymentSupportAllowance.map(_.decrypted[Boolean]),
      taxableStateBenefit = taxableStateBenefit.map(_.decrypted[Boolean]),
      taxRefundOrOffset = taxRefundOrOffset.map(_.decrypted[Boolean])
    )
}

object EncryptedWorkBenefitsModel {
  implicit lazy val encryptedValueOFormat: OFormat[EncryptedValue] = Json.format[EncryptedValue]
  implicit lazy val formats: OFormat[EncryptedWorkBenefitsModel] = Json.format[EncryptedWorkBenefitsModel]
}
