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

case class PensionsPaymentsModel(
                                  ukPensionsPayments: Option[Boolean] = None ,
                                  nonUkPensionsPayments: Option[Boolean] = None,
                                  overseasTransferCharges: Option[Boolean] = None

                                ) {
  def encrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): EncryptedPensionsPaymentsModel =
    EncryptedPensionsPaymentsModel(
      ukPensionsPayments = ukPensionsPayments.map(_.encrypted),
      nonUkPensionsPayments = nonUkPensionsPayments.map(_.encrypted),
      overseasTransferCharges = overseasTransferCharges.map(_.encrypted)
    )
}

object PensionsPaymentsModel {
  implicit lazy val formats: OFormat[PensionsPaymentsModel] = Json.format[PensionsPaymentsModel]
}
case class EncryptedPensionsPaymentsModel(
                                  ukPensionsPayments: Option[EncryptedValue] = None,
                                  nonUkPensionsPayments: Option[EncryptedValue] = None,
                                  overseasTransferCharges: Option[EncryptedValue] = None

                                ) {
  def decrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): PensionsPaymentsModel =
    PensionsPaymentsModel(
      ukPensionsPayments = ukPensionsPayments.map(_.decrypted[Boolean]),
      nonUkPensionsPayments = nonUkPensionsPayments.map(_.decrypted[Boolean]),
      overseasTransferCharges = overseasTransferCharges.map(_.decrypted[Boolean])
    )
}

object EncryptedPensionsPaymentsModel {
  implicit lazy val encryptedValueOFormat: OFormat[EncryptedValue] = Json.format[EncryptedValue]
  implicit lazy val formats: OFormat[EncryptedPensionsPaymentsModel] = Json.format[EncryptedPensionsPaymentsModel]
}
