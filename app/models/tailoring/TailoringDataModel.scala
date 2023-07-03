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
import utils.AesGcmAdCrypto

case class TailoringDataModel(
                               aboutYou: Option[AboutYouModel] = None,
                               workBenefits: Option[WorkBenefitsModel] = None,
                               rentalsPensionsInvestments: Option[RentalsPensionsInvestmentsModel] = None,
                               gainsTrustsEstates: Option[GainsTrustsEstatesModel] = None,
                               pensionsPayments: Option[PensionsPaymentsModel] = None,
                               allowancesTaxRelief: Option[AllowancesTaxReliefModel] = None
                        ) {
  def encrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): EncryptedTailoringDataModel =
    EncryptedTailoringDataModel(
      aboutYou.map(_.encrypted),
      workBenefits.map(_.encrypted),
      rentalsPensionsInvestments.map(_.encrypted),
      gainsTrustsEstates.map(_.encrypted),
      pensionsPayments.map(_.encrypted),
      allowancesTaxRelief.map(_.encrypted)
    )

   def updateFrom(tailoringDataModel: TailoringDataModel): TailoringDataModel ={
     this.copy(
       aboutYou = tailoringDataModel.aboutYou.fold(this.aboutYou)(data => Some(data)),
       workBenefits = tailoringDataModel.workBenefits.fold(this. workBenefits)(data => Some(data)),
       rentalsPensionsInvestments = tailoringDataModel.rentalsPensionsInvestments.fold(this.rentalsPensionsInvestments)(data => Some(data)),
       gainsTrustsEstates = tailoringDataModel.gainsTrustsEstates.fold(this.gainsTrustsEstates)(data => Some(data)),
       pensionsPayments = tailoringDataModel.pensionsPayments.fold(this.pensionsPayments)(data => Some(data)),
       allowancesTaxRelief = tailoringDataModel.allowancesTaxRelief.fold(this.allowancesTaxRelief)(data => Some(data))
     )
   }
}

object TailoringDataModel {
  implicit lazy val formats: OFormat[TailoringDataModel] = Json.format[TailoringDataModel]
}

case class EncryptedTailoringDataModel(
                               aboutYou: Option[EncryptedAboutYouModel] = None,
                               workBenefits: Option[EncryptedWorkBenefitsModel] = None,
                               rentalsPensionsInvestments: Option[EncryptedRentalsPensionsInvestmentsModel] = None,
                               gainsTrustsEstates: Option[EncryptedGainsTrustsEstatesModel] = None,
                               pensionsPayments: Option[EncryptedPensionsPaymentsModel] = None,
                               allowancesTaxRelief: Option[EncryptedAllowancesTaxReliefModel] = None
                        ) {
  def decrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): TailoringDataModel =
    TailoringDataModel(
      aboutYou.map(_.decrypted),
      workBenefits.map(_.decrypted),
      rentalsPensionsInvestments.map(_.decrypted),
      gainsTrustsEstates.map(_.decrypted),
      pensionsPayments.map(_.decrypted),
      allowancesTaxRelief.map(_.decrypted)
    )
}

object EncryptedTailoringDataModel {
  implicit lazy val formats: OFormat[EncryptedTailoringDataModel] = Json.format[EncryptedTailoringDataModel]
}
