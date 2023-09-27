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

import play.api.libs.json._
import uk.gov.hmrc.crypto.Sensitive._
import uk.gov.hmrc.crypto.json.JsonEncryption
import uk.gov.hmrc.crypto.{Decrypter, Encrypter}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

case class UserData(
                              mtdItId: String,
                              taxYear: Int,
                              data: JsObject,
                              lastUpdated: Instant
                            ) {

}
  object UserData {

    val reads: Reads[UserData] = {

      import play.api.libs.functional.syntax._

      (
        (__ \ "mtdItId").read[String] and
          (__ \ "taxYear").read[Int] and
          (__ \ "data").read[JsObject] and
          (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
        ) (UserData.apply _)
    }

    val writes: OWrites[UserData] = {

      import play.api.libs.functional.syntax._

      (
        (__ \ "mtdItId").write[String] and
          (__ \ "taxYear").write[Int] and
          (__ \ "data").write[JsObject] and
          (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
        ) (unlift(UserData.unapply))
    }

    implicit val format: OFormat[UserData] = OFormat(reads, writes)

    def encryptedFormat(implicit crypto: Encrypter with Decrypter): OFormat[UserData] = {

      import play.api.libs.functional.syntax._

      implicit val sensitiveFormat: Format[SensitiveString] =
        JsonEncryption.sensitiveEncrypterDecrypter(SensitiveString.apply)

      val encryptedReads: Reads[UserData] =
        (
          (__ \ "mtdItId").read[String] and
            (__ \ "taxYear").read[Int] and
            (__ \ "data").read[SensitiveString] and
            (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
          )((mtdItId, taxYear, data, lastUpdated) => UserData(mtdItId, taxYear, Json.parse(data.decryptedValue).as[JsObject], lastUpdated))

      val encryptedWrites: OWrites[UserData] =
        (
          (__ \ "mtdItId").write[String] and
            (__ \ "taxYear").write[Int] and
            (__ \ "data").write[SensitiveString] and
            (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
          )(ua => (ua.mtdItId, ua.taxYear, SensitiveString(Json.stringify(ua.data)), ua.lastUpdated))

      OFormat(encryptedReads orElse reads, encryptedWrites)
    }
  }
