package store

import java.util.Date
import config.Config
import helpers.Time
import scala.collection.mutable.ArrayBuffer


object StoreHelper {
  def makeStore(config: Config): StoreClient = new StoreClient(config)
}

// Receives data from the main process and sends them to the DB at the end of the experiment
class StoreClient(config: Config,
                  toDataBase: ExperimentData => Unit = DataBaseMD.storeDocument(_)) extends Time {

  var startTime: Date = null
  var endTime: Date = null
  var values = ArrayBuffer.empty[Double]
  var timeStamps = ArrayBuffer.empty[Date]

  def startExperiment: Unit = startTime = getTime

  def endExperiment: Unit = {
    endTime = getTime
    storeExperimentToDataBase
  }

  def storeValue(value: Double): Unit = {
    val date: Date = getTime
    values.append(value)
    timeStamps.append(date)
  }

  private def storeExperimentToDataBase = {
    val experiment = ExperimentData(startTime, endTime, config, values, timeStamps)
    toDataBase(experiment)
  }
}

