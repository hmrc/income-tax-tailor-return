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

package support

import controllers.predicates.{FakeAuthorisedAction, IdentifierAction}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.PlayBodyParsers
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.stubControllerComponents
import support.providers.AppConfigStubProvider

trait UnitTest extends AnyWordSpec with DefaultAwaitTimeout
  with Matchers with AppConfigStubProvider with BeforeAndAfterEach {


  val parsers: PlayBodyParsers = stubControllerComponents().parsers
  protected def applicationBuilder(isAgent: Option[String] = None): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[IdentifierAction].to(new FakeAuthorisedAction(isAgent)(parsers))
      )

}