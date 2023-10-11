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

import play.core.PlayVersion.current
import sbt._


object AppDependencies {

  private val bootstrapVersion = "7.22.0"
  private val hmrcMongoVersion = "1.3.0"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"  % bootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"         % hmrcMongoVersion,
    "com.fasterxml.jackson.module"  %% "jackson-module-scala" % "2.14.2",
    "uk.gov.hmrc"             %% "crypto-json-play-28"          % "7.3.0",
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % bootstrapVersion % "test, it",
    "com.typesafe.play" %% "play-test" % current % Test,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-28" % hmrcMongoVersion % Test,
    "org.mockito"   %% "mockito-scala"      % "1.17.12",
    "org.scalatest" %% "scalatest" % "3.2.15" % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.64.0" % "test, it",
    "com.github.tomakehurst" % "wiremock-jre8" % "2.35.0" % "test, it"
  )
}
