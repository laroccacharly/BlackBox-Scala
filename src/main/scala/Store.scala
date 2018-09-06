/*
  Store receives data from Master and store data to DataBase with experiment ends.
 */

import java.util.Date

import akka.actor.{Actor, ActorRef, Props}
import config.Config
import helpers.Time

object Store {
  case object StartOfExperiment
  case class EndOfExperiment(optimizationResults: OptimizationResults)
  def props(config: Config, killSwitch: ActorRef, toDataBase: ExperimentData => Unit = DataBaseMD.storeDocument(_)) = Props(new Store(config, killSwitch, toDataBase))
}

class Store(config: Config,  killSwitch: ActorRef, toDataBase: ExperimentData => Unit) extends Actor with Time {
  import Store._

  var startTime: Date = null
  var endTime: Date = null

  override def receive = {
    case StartOfExperiment => startTime = getTime
    case EndOfExperiment(results) => {
      endTime = getTime
      storeToDataBase(results)
      killSwitch ! KillSwitch.ShutdownMessage
    }
  }

  def storeToDataBase(results: OptimizationResults) = {
    val experimentData = ExperimentData(startTime, endTime, config, results)
    toDataBase(experimentData)
  }

}
