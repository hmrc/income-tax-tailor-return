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

package models.mongo

import models.tailoring.{EncryptedTailoringDataModel, TailoringDataModel}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.mongo.play.json.formats.MongoJodaFormats.dateTimeFormat
import utils.AesGcmAdCrypto

case class TailoringUserData(
                              nino: String,
                              taxYear: Int,
                              tailoring: TailoringDataModel,
                              lastUpdated: DateTime = DateTime.now(DateTimeZone.UTC)
                            ) {
  def encrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): EncryptedTailoringUserData =
    EncryptedTailoringUserData(
      nino = nino,
      taxYear = taxYear,
      tailoring.encrypted,
      lastUpdated = lastUpdated
    )

  def updateFrom(updatedModel: TailoringDataModel): TailoringUserData = {
    this.copy(tailoring = tailoring.updateFrom(updatedModel))
  }
}

object TailoringUserData {
  implicit val mongoJodaDateTimeFormats: Format[DateTime] = dateTimeFormat

  implicit lazy val formats: OFormat[TailoringUserData] = Json.format[TailoringUserData]

}

case class EncryptedTailoringUserData(
                                       nino: String,
                                       taxYear: Int,
                                       tailoring: EncryptedTailoringDataModel,
                                       lastUpdated: DateTime = DateTime.now(DateTimeZone.UTC)
                                     ){
  def decrypted(implicit aesGcmAdCrypto: AesGcmAdCrypto, associatedText: String): TailoringUserData =
    TailoringUserData(
      nino = nino,
      taxYear = taxYear,
      tailoring.decrypted,
      lastUpdated = lastUpdated
    )
}

object EncryptedTailoringUserData {
  implicit val mongoJodaDateTimeFormats: Format[DateTime] = dateTimeFormat
  implicit lazy val formats: OFormat[EncryptedTailoringUserData] = Json.format[EncryptedTailoringUserData]

}
