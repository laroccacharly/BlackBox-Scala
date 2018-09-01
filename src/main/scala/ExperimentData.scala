import java.util.Date
import config.Config

// Model of data that is stored in DB
case class ExperimentData(startTime: Date,
                          endTime: Date,
                          config: Config,
                          results: OptimizationResults){

  def executionTime: Double = {
    endTime.getTime - startTime.getTime // milliseconds
  }

  def accuracy: Double = {
    math.abs(config.goal - results.bestObservation.input)
  }

  def name: String = config.experimentName
}
