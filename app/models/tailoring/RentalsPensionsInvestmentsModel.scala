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


case class PropertyModel(
                          ukProperty: Option[Boolean] = None,
                          nonUkProperty: Option[Boolean] = None
                        ){
  def encrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): EncryptedPropertyModel =
    EncryptedPropertyModel(
      ukProperty = ukProperty.map(_.encrypted),
      nonUkProperty = nonUkProperty.map(_.encrypted)
    )
}

object PropertyModel {
  implicit lazy val formats: OFormat[PropertyModel] = Json.format[PropertyModel]
}

case class EncryptedPropertyModel(
                          ukProperty: Option[EncryptedValue] = None,
                          nonUkProperty: Option[EncryptedValue] = None
                        ){
  def decrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): PropertyModel =
    PropertyModel(
      ukProperty = ukProperty.map(_.decrypted[Boolean]),
      nonUkProperty = nonUkProperty.map(_.decrypted[Boolean])
    )
}

object EncryptedPropertyModel {
  implicit lazy val encryptedValueOFormat: OFormat[EncryptedValue] = Json.format[EncryptedValue]
  implicit lazy val formats: OFormat[EncryptedPropertyModel] = Json.format[EncryptedPropertyModel]
}


case class RentalsPensionsInvestmentsModel(
                                            property: Option[PropertyModel] = None,
                                            statePensions: Option[Boolean] = None,
                                            otherUkPensions: Option[Boolean] = None,
                                            unAuthorisedPaymentsFromPensions: Option[Boolean] = None,
                                            shortServiceRefundsFromPensions: Option[Boolean] = None,
                                            nonUkPensions: Option[Boolean] = None,
                                            lifeInsurance: Option[Boolean] = None,
                                            lifeAnnuity: Option[Boolean] = None,
                                            capitalRedemption: Option[Boolean] = None,
                                            voidedIsa: Option[Boolean] = None,
                                            interestFromUkBanksSocieties: Option[Boolean] = None,
                                            interestFromUkTrustFundBonds: Option[Boolean] = None,
                                            interestFromGiltEdgedAccruedSecurities: Option[Boolean] = None,
                                            ukDividendsStocksShares: Option[Boolean] = None,
                                            ukStockDividends: Option[Boolean] = None,
                                            dividendsUnitTrustsInvestment: Option[Boolean] = None,
                                            freeRedeemableShares: Option[Boolean] = None,
                                            closeCompanyLoans: Option[Boolean] = None,
                                            nonUkInterest: Option[Boolean] = None,
                                            nonUkDividendsInUk: Option[Boolean] = None,
                                            nonUkDividendsAbroad: Option[Boolean] = None,
                                            nonUkInsurancePolicies: Option[Boolean] = None
                                          ) {

  def encrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): EncryptedRentalsPensionsInvestmentsModel =
    EncryptedRentalsPensionsInvestmentsModel(
      property = property.map(_.encrypted),
      statePensions = statePensions.map(_.encrypted),
      otherUkPensions = otherUkPensions.map(_.encrypted),
      unAuthorisedPaymentsFromPensions = unAuthorisedPaymentsFromPensions.map(_.encrypted),
      shortServiceRefundsFromPensions = shortServiceRefundsFromPensions.map(_.encrypted),
      nonUkPensions = nonUkPensions.map(_.encrypted),
      lifeInsurance = lifeInsurance.map(_.encrypted),
      lifeAnnuity = lifeAnnuity.map(_.encrypted),
      capitalRedemption = capitalRedemption.map(_.encrypted),
      voidedIsa = voidedIsa.map(_.encrypted),
      interestFromUkBanksSocieties = interestFromUkBanksSocieties.map(_.encrypted),
      interestFromUkTrustFundBonds = interestFromUkTrustFundBonds.map(_.encrypted),
      interestFromGiltEdgedAccruedSecurities = interestFromGiltEdgedAccruedSecurities.map(_.encrypted),
      ukDividendsStocksShares = ukDividendsStocksShares.map(_.encrypted),
      ukStockDividends = ukStockDividends.map(_.encrypted),
      dividendsUnitTrustsInvestment = dividendsUnitTrustsInvestment.map(_.encrypted),
      freeRedeemableShares = freeRedeemableShares.map(_.encrypted),
      closeCompanyLoans = closeCompanyLoans.map(_.encrypted),
      nonUkInterest = nonUkInterest.map(_.encrypted),
      nonUkDividendsInUk = nonUkDividendsInUk.map(_.encrypted), nonUkDividendsAbroad = nonUkDividendsAbroad.map(_.encrypted),
      nonUkInsurancePolicies = nonUkInsurancePolicies.map(_.encrypted)
    )
}

object RentalsPensionsInvestmentsModel {
  implicit lazy val formats: OFormat[RentalsPensionsInvestmentsModel] = Json.format[RentalsPensionsInvestmentsModel]
}

case class EncryptedRentalsPensionsInvestmentsModel(
                                            property: Option[EncryptedPropertyModel] = None,
                                            statePensions: Option[EncryptedValue] = None,
                                            otherUkPensions: Option[EncryptedValue] = None,
                                            unAuthorisedPaymentsFromPensions: Option[EncryptedValue] = None,
                                            shortServiceRefundsFromPensions: Option[EncryptedValue] = None,
                                            nonUkPensions: Option[EncryptedValue] = None,
                                            lifeInsurance: Option[EncryptedValue] = None,
                                            lifeAnnuity: Option[EncryptedValue] = None,
                                            capitalRedemption: Option[EncryptedValue] = None,
                                            voidedIsa: Option[EncryptedValue] = None,
                                            interestFromUkBanksSocieties: Option[EncryptedValue] = None,
                                            interestFromUkTrustFundBonds: Option[EncryptedValue] = None,
                                            interestFromGiltEdgedAccruedSecurities: Option[EncryptedValue] = None,
                                            ukDividendsStocksShares: Option[EncryptedValue] = None,
                                            ukStockDividends: Option[EncryptedValue] = None,
                                            dividendsUnitTrustsInvestment: Option[EncryptedValue] = None,
                                            freeRedeemableShares: Option[EncryptedValue] = None,
                                            closeCompanyLoans: Option[EncryptedValue] = None,
                                            nonUkInterest: Option[EncryptedValue] = None,
                                            nonUkDividendsInUk: Option[EncryptedValue] = None,
                                            nonUkDividendsAbroad: Option[EncryptedValue] = None,
                                            nonUkInsurancePolicies: Option[EncryptedValue] = None
                                          ) {
  def decrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): RentalsPensionsInvestmentsModel =
    RentalsPensionsInvestmentsModel(
      property = property.map(_.decrypted),
      statePensions = statePensions.map(_.decrypted[Boolean]),
      otherUkPensions = otherUkPensions.map(_.decrypted[Boolean]),
      unAuthorisedPaymentsFromPensions = unAuthorisedPaymentsFromPensions.map(_.decrypted[Boolean]),
      shortServiceRefundsFromPensions = shortServiceRefundsFromPensions.map(_.decrypted[Boolean]),
      nonUkPensions = nonUkPensions.map(_.decrypted[Boolean]),
      lifeInsurance = lifeInsurance.map(_.decrypted[Boolean]),
      lifeAnnuity = lifeAnnuity.map(_.decrypted[Boolean]),
      capitalRedemption = capitalRedemption.map(_.decrypted[Boolean]),
      voidedIsa = voidedIsa.map(_.decrypted[Boolean]),
      interestFromUkBanksSocieties = interestFromUkBanksSocieties.map(_.decrypted[Boolean]),
      interestFromUkTrustFundBonds = interestFromUkTrustFundBonds.map(_.decrypted[Boolean]),
      interestFromGiltEdgedAccruedSecurities = interestFromGiltEdgedAccruedSecurities.map(_.decrypted[Boolean]),
      ukDividendsStocksShares = ukDividendsStocksShares.map(_.decrypted[Boolean]),
      ukStockDividends = ukStockDividends.map(_.decrypted[Boolean]),
      dividendsUnitTrustsInvestment = dividendsUnitTrustsInvestment.map(_.decrypted[Boolean]),
      freeRedeemableShares = freeRedeemableShares.map(_.decrypted[Boolean]),
      closeCompanyLoans = closeCompanyLoans.map(_.decrypted[Boolean]),
      nonUkInterest = nonUkInterest.map(_.decrypted[Boolean]),
      nonUkDividendsInUk = nonUkDividendsInUk.map(_.decrypted[Boolean]),
      nonUkDividendsAbroad = nonUkDividendsAbroad.map(_.decrypted[Boolean]),
      nonUkInsurancePolicies = nonUkInsurancePolicies.map(_.decrypted[Boolean])
    )
}

object EncryptedRentalsPensionsInvestmentsModel {
  implicit lazy val encryptedValueOFormat: OFormat[EncryptedValue] = Json.format[EncryptedValue]
  implicit lazy val formats: OFormat[EncryptedRentalsPensionsInvestmentsModel] = Json.format[EncryptedRentalsPensionsInvestmentsModel]
}

