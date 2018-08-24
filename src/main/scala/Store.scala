import java.util.Date

import akka.actor.{Actor, ActorRef, Props}
import config.Config
import helpers.Time

object Store {
  case object StartOfExperiment
  case class EndOfExperiment(optimizationResults: OptimizationResults)
  def props(config: Config) = Props(new Store(config))
}

class Store(config: Config,  toDataBase: ExperimentData => Unit = DataBaseMD.storeDocument(_)) extends Actor with Time {
  import Store._

  var startTime: Date = null
  var endTime: Date = null

  override def receive = {
    case StartOfExperiment => startTime = getTime
    case EndOfExperiment(results) => {
      endTime = getTime
      storeToDataBase(results)
    }
  }

  def storeToDataBase(results: OptimizationResults) = {
    val experimentData = ExperimentData(startTime, endTime, config, results)
    toDataBase(experimentData)
  }

}
