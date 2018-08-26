
import config.Config
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters.{equal, exists}
import org.mongodb.scala.{Completed, MongoClient, MongoCollection, MongoDatabase, Observer, result}
import scala.concurrent._

// Setup connections to the database.
trait MongoConfig {
  val codecRegistry = fromRegistries(fromProviders(classOf[ExperimentData], classOf[Config], classOf[OptimizationResults], classOf[Observation]), DEFAULT_CODEC_REGISTRY )
  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("testDb").withCodecRegistry(codecRegistry)
  val collection: MongoCollection[ExperimentData] = database.getCollection("testCollection")
}


// Some default behaviors for the Observer class
class CustomObserver[T] extends Observer[T] {
  override def onNext(result: T): Unit = ???
  override def onError(e: Throwable): Unit = println(e.getMessage)
  override def onComplete(): Unit = println("Completed")
}


object DataBaseMD extends MongoConfig {

  def count: Unit  = {
    collection.countDocuments().subscribe(
      new CustomObserver[Long] {
        override def onNext(result: Long): Unit = println(s"count is $result")
      }
    )
  }

  def deleteAll: Unit  = {
    collection.deleteMany(exists("_id")).subscribe(
      new CustomObserver[result.DeleteResult] {
        override def onNext(r: result.DeleteResult): Unit = {
          val count = r.getDeletedCount
          println(s"deleted $count documents")
        }
      }
    )
  }

  def withExperimentData(experimentName: String): Future[List[ExperimentData]] = {
    var experimentDatas: List[ExperimentData] = List.empty[ExperimentData]
    def appendExperimentData(experimentData: ExperimentData) = experimentDatas = experimentData :: experimentDatas

    val promise = Promise[List[ExperimentData]]

    collection.find(equal("config.experimentName", experimentName)).subscribe(
      new CustomObserver[ExperimentData] {
        override def onNext(result: ExperimentData): Unit = appendExperimentData(result)
        override def onComplete(): Unit = promise.success(experimentDatas)
      })

    promise.future
  }

  def storeDocument(doc: ExperimentData): Unit  = {
    collection.insertOne(doc).subscribe(
      new CustomObserver[Completed] {
        override def onNext(result: Completed): Unit = println("Inserted")
      })
  }
}
