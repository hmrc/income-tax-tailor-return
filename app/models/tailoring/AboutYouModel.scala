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

import play.api.libs.json._
import uk.gov.hmrc.crypto.EncryptedValue
import utils.AesGcmAdCrypto
import utils.CypherSyntax.{DecryptableOps, EncryptableOps}

case class AboutYouModel(
                          ukResidentBetweenTaxYear: Option[Boolean] = None,
                          wasDomicile: Option[Boolean] = None,
                          dualResident: Option[Boolean] = None,
                          donationsUsingGiftAid: Option[Boolean] = None,
                          giftsSharesSecurities: Option[Boolean] = None,
                          giftsLandProperty: Option[Boolean] = None,
                          transferMarriageAllowance: Option[Boolean] = None,
                          childBenefit: Option[Boolean] = None,
                          wasIncomeOverAmount: Option[Boolean] = None,
                          hasHigherIncomeThanPartner: Option[Boolean] = None,
                          fosterOrSharedLifeCarer: Option[Boolean] = None,
                          patentRoyaltyPayments: Option[Boolean] = None,
                          taxAvoidance: Option[Boolean] = None,
                          disguisedRemuneration: Option[Boolean] = None
                        ) {
  def encrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): EncryptedAboutYouModel =
    EncryptedAboutYouModel(
      ukResidentBetweenTaxYear = ukResidentBetweenTaxYear.map(_.encrypted),
      wasDomicile = wasDomicile.map(_.encrypted),
      dualResident = dualResident.map(_.encrypted),
      donationsUsingGiftAid = donationsUsingGiftAid.map(_.encrypted),
      giftsSharesSecurities = giftsSharesSecurities.map(_.encrypted),
      giftsLandProperty = giftsLandProperty.map(_.encrypted),
      transferMarriageAllowance = transferMarriageAllowance.map(_.encrypted),
      childBenefit = childBenefit.map(_.encrypted),
      wasIncomeOverAmount = wasIncomeOverAmount.map(_.encrypted),
      hasHigherIncomeThanPartner = hasHigherIncomeThanPartner.map(_.encrypted),
      fosterOrSharedLifeCarer = fosterOrSharedLifeCarer.map(_.encrypted),
      patentRoyaltyPayments = patentRoyaltyPayments.map(_.encrypted),
      taxAvoidance = taxAvoidance.map(_.encrypted),
      disguisedRemuneration = disguisedRemuneration.map(_.encrypted)
    )
}

object AboutYouModel {
  implicit lazy val formats: OFormat[AboutYouModel] = Json.format[AboutYouModel]
}

case class EncryptedAboutYouModel(
                                   ukResidentBetweenTaxYear: Option[EncryptedValue] = None,
                                   wasDomicile: Option[EncryptedValue] = None,
                                   dualResident: Option[EncryptedValue] = None,
                                   donationsUsingGiftAid: Option[EncryptedValue] = None,
                                   giftsSharesSecurities: Option[EncryptedValue] = None,
                                   giftsLandProperty: Option[EncryptedValue] = None,
                                   transferMarriageAllowance: Option[EncryptedValue] = None,
                                   childBenefit: Option[EncryptedValue] = None,
                                   wasIncomeOverAmount: Option[EncryptedValue] = None,
                                   hasHigherIncomeThanPartner: Option[EncryptedValue] = None,
                                   fosterOrSharedLifeCarer: Option[EncryptedValue] = None,
                                   patentRoyaltyPayments: Option[EncryptedValue] = None,
                                   taxAvoidance: Option[EncryptedValue] = None,
                                   disguisedRemuneration: Option[EncryptedValue] = None
                                 ) {
  def decrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): AboutYouModel =
    AboutYouModel(
      ukResidentBetweenTaxYear = ukResidentBetweenTaxYear.map(_.decrypted[Boolean]),
      wasDomicile = wasDomicile.map(_.decrypted[Boolean]),
      dualResident = dualResident.map(_.decrypted[Boolean]),
      donationsUsingGiftAid = donationsUsingGiftAid.map(_.decrypted[Boolean]),
      giftsSharesSecurities = giftsSharesSecurities.map(_.decrypted[Boolean]),
      giftsLandProperty = giftsLandProperty.map(_.decrypted[Boolean]),
      transferMarriageAllowance = transferMarriageAllowance.map(_.decrypted[Boolean]),
      childBenefit = childBenefit.map(_.decrypted[Boolean]),
      wasIncomeOverAmount = wasIncomeOverAmount.map(_.decrypted[Boolean]),
      hasHigherIncomeThanPartner = hasHigherIncomeThanPartner.map(_.decrypted[Boolean]),
      fosterOrSharedLifeCarer = fosterOrSharedLifeCarer.map(_.decrypted[Boolean]),
      patentRoyaltyPayments = patentRoyaltyPayments.map(_.decrypted[Boolean]),
      taxAvoidance = taxAvoidance.map(_.decrypted[Boolean]),
      disguisedRemuneration = disguisedRemuneration.map(_.decrypted[Boolean])
    )
}

object EncryptedAboutYouModel {
  implicit lazy val encryptedValueOFormat: OFormat[EncryptedValue] = Json.format[EncryptedValue]
  implicit lazy val formats: OFormat[EncryptedAboutYouModel] = Json.format[EncryptedAboutYouModel]
}
