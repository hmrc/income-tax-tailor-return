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

package repositories

import config.AppConfig
import models.Done
import models.mongo.TaskListData
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.model._
import play.api.Logging
import play.api.libs.json.Format
import uk.gov.hmrc.crypto.{Decrypter, Encrypter}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs.toBson
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.{Clock, Instant}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TaskListDataRepository @Inject()(
  mongoComponent: MongoComponent,
  appConfig: AppConfig,
  clock: Clock
)(implicit ec: ExecutionContext, crypto: Encrypter with Decrypter)
  extends PlayMongoRepository[TaskListData](
    collectionName = "taskListData",
    mongoComponent = mongoComponent,
    domainFormat   = TaskListData.encryptedFormat,
    indexes = RepositoryIndexes.indexes()(appConfig),
    replaceIndexes = appConfig.replaceIndexes
  ) with Logging {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def filterByMtdItIdYear(mtdItId: String, taxYear: Int): Bson = and(
    equal("mtdItId", toBson(mtdItId)),
    equal("taxYear", toBson(taxYear))
  )

  def keepAlive(mtdItId: String, taxYear: Int): Future[Done] =
    collection
      .updateOne(
        filter = filterByMtdItIdYear(mtdItId, taxYear),
        update = Updates.set("lastUpdated", Instant.now(clock))
      )
      .toFuture()
      .map(_ => Done)

  def get(mtdItId: String, taxYear: Int): Future[Option[TaskListData]] = {
    keepAlive(mtdItId, taxYear).flatMap {
      _ =>
        collection
          .find(filterByMtdItIdYear(mtdItId, taxYear))
          .headOption()
    }
  }

  def set(taskListData: TaskListData): Future[Done] = {
    val updatedTaskListData = taskListData copy (lastUpdated = Instant.now(clock))

    collection
      .replaceOne(
        filter = filterByMtdItIdYear(updatedTaskListData.mtdItId, updatedTaskListData.taxYear),
        replacement = updatedTaskListData,
        options = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => Done)
  }

  def clear(mtdItId: String, taxYear: Int): Future[Done] =
    collection
      .deleteOne(filterByMtdItIdYear(mtdItId, taxYear))
      .toFuture()
      .map(_ => Done)
}
