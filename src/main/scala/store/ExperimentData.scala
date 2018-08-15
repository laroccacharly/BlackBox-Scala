package store

import java.util.Date
import config.Config

// Model of data that is stored in DB
case class ExperimentData(startTime: Date,
                          endTime: Date,
                          config: Config,
                          values: Iterable[Double] = Iterable.empty[Double],
                          timeStamps: Iterable[Date] = Iterable.empty[Date]){

  def executionTime: Double = {
    endTime.getTime - startTime.getTime // milliseconds
  }
}