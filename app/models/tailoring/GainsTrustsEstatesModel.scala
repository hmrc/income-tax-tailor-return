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

case class GainsTrustsEstatesModel(
                                    capitalGains: Option[Boolean] = None,
                                    trusts: Option[Boolean] = None,
                                    settlements: Option[Boolean] = None,
                                    estatesOfDead: Option[Boolean] = None

                                  ) {
  def encrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): EncryptedGainsTrustsEstatesModel =
    EncryptedGainsTrustsEstatesModel(
      capitalGains = capitalGains.map(_.encrypted),
      trusts = trusts.map(_.encrypted),
      settlements = settlements.map(_.encrypted),
      estatesOfDead = estatesOfDead.map(_.encrypted)
    )
}

object GainsTrustsEstatesModel {
  implicit lazy val formats: OFormat[GainsTrustsEstatesModel] = Json.format[GainsTrustsEstatesModel]
}

case class EncryptedGainsTrustsEstatesModel(
                                    capitalGains: Option[EncryptedValue] = None,
                                    trusts: Option[EncryptedValue] = None,
                                    settlements: Option[EncryptedValue] = None,
                                    estatesOfDead: Option[EncryptedValue] = None

                                  ) {
  def decrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): GainsTrustsEstatesModel =
    GainsTrustsEstatesModel(
      capitalGains = capitalGains.map(_.decrypted[Boolean]),
      trusts = trusts.map(_.decrypted[Boolean]),
      settlements = settlements.map(_.decrypted[Boolean]),
      estatesOfDead = estatesOfDead.map(_.decrypted[Boolean])
    )
}

object EncryptedGainsTrustsEstatesModel {
  implicit lazy val encryptedValueOFormat: OFormat[EncryptedValue] = Json.format[EncryptedValue]
  implicit lazy val formats: OFormat[EncryptedGainsTrustsEstatesModel] = Json.format[EncryptedGainsTrustsEstatesModel]
}
