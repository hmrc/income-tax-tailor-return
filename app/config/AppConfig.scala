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

package config

import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration

@Singleton
class AppConfig @Inject()(config: ServicesConfig) {
  lazy val useEncryption: Boolean = config.getBoolean("mongodb.useEncryption")
  lazy val encryptionKey: String = config.getString("mongodb.encryption.key")
  lazy val employmentBaseUrl: String = config.baseUrl("income-tax-employment")

  lazy val ifAuthorisationToken: String = "microservice.services.integration-framework.authorisation-token"
  lazy val ifBaseUrl: String = config.baseUrl(serviceName = "integration-framework")
  lazy val ifEnvironment: String = config.getString(key = "microservice.services.integration-framework.environment")
  def mongoTTL: Long = Duration(config.getString("mongodb.timeToLive")).toDays.toInt
  def authorisationTokenFor(api: String): String = config.getString(s"microservice.services.integration-framework.authorisation-token.$api")

}
