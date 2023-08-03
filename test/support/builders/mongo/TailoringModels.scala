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

package support.builders.mongo

import models.tailoring._

object TailoringModels {
  val aAboutYou: AboutYouModel = AboutYouModel(
    Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true), Some(true))

  val aWorkBenefitModel: WorkBenefitsModel = WorkBenefitsModel(Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true)
    ,Some(true),Some(true),Some(true),Some(true),Some(true))

  val aGainsTrustsEstatesModel: GainsTrustsEstatesModel = GainsTrustsEstatesModel(Some(true),Some(true),Some(true),Some(true))

  val aRentalsPensionsInvestmentsModel: RentalsPensionsInvestmentsModel =
    RentalsPensionsInvestmentsModel(Some(PropertyModel(Some(true),Some(true))),Some(true),Some(true),Some(true),Some(true),Some(true), Some(true)
      ,Some(true),Some(true),Some(true),Some(true) ,Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),Some(true))

  val aPensionsPaymentsModel: PensionsPaymentsModel = PensionsPaymentsModel(Some(true),Some(true),Some(true))

  val aAllowancesTaxReliefModel : AllowancesTaxReliefModel = AllowancesTaxReliefModel(Some(true),Some(true),Some(true),Some(true),Some(true),Some(true),
    Some(true),Some(true),Some(true),Some(true),Some(true),Some(true))

  val aTailoringDataModel: TailoringDataModel =
    TailoringDataModel(Some(aAboutYou), Some(aWorkBenefitModel), Some(aRentalsPensionsInvestmentsModel),
      Some(aGainsTrustsEstatesModel), Some(aPensionsPaymentsModel), Some(aAllowancesTaxReliefModel)
    )

}
