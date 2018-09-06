package config
import scala.util.Random._

// Configuration class to control the behavior of the program
case class Config(domainMin: Double,
                  domainMax: Double,
                  goal: Double,
                  functionName: String,
                  epsilon: Double,
                  nbWorkers: Int,
                  nbIter: Int,
                  stoppingCriteria: Double,
                  pullingPeriod: Int,
                  dampening: Double, // dampening * newProgress + (1 - dampening) * lastProgress
                  experimentName: String
                 ) {


  def f: Double => Double = {
    // functions cannot be stored in a DB. So we use a switch statement.
    functionName match {
      case "square" => x => (x - 1) * (x - 1)
    }
  }

  def takeRandom[T](list: List[T]) = shuffle(list).head
  def takeRandomInt(min: Int, max: Int) = takeRandom((min until max + 1).toList)
  def takeRandomDouble(min: Double, max: Double) = math.random() * (max - min) + min

  // A config can be changed with one of these functions.
  // These functions modify the experiment name to help keep track what changes were made.

  def withNbWorkers(nbWorkers: Int = takeRandomInt(1, 30)): Config = {
    copy(
      nbWorkers = nbWorkers,
      experimentName = experimentName + "W" + nbWorkers.toString
    )
  }

  def withPullingPeriod(pullingPeriod: Int = takeRandomInt(1, 25)): Config = {
    copy(
      pullingPeriod = pullingPeriod,
      experimentName = experimentName + "P" + pullingPeriod.toString
    )
  }

  def withEpsilon(epsilon: Double = takeRandomDouble(0.5, 0.99)): Config = {
    copy(
      epsilon = epsilon,
      experimentName = experimentName + "E" + epsilon.toString
    )
  }

  def withDampening(dampening: Double = takeRandomDouble(0.7, 0.99)): Config = {
    copy(
      dampening = dampening,
      experimentName = experimentName + "D" + dampening.toString
    )
  }

  def withNbIter(nbIter: Int = takeRandomInt(5, 100)): Config = {
    copy(
      nbIter = nbIter,
      experimentName = experimentName + "I" + nbIter.toString
    )
  }

  def withStoppingCriteria(stoppingCriteria: Double = takeRandomDouble(0.05, 0.5)): Config = {
    copy(
      stoppingCriteria = stoppingCriteria,
      experimentName = experimentName + "S" + stoppingCriteria.toString
    )
  }
}

trait ConfigFactory {

  def defaultConfig = Config(
    domainMin = 0,
    domainMax = 2,
    goal = 1,
    functionName = "square",
    epsilon = 0.5,
    nbWorkers = 1,
    nbIter = 10,
    stoppingCriteria = 0.3,
    pullingPeriod = 25,
    experimentName = "square",
    dampening = 0.8
  )

  // All possible experiment configs

  // run -e squareW1P1D0.99E0.9,squareW10P1D0.99E0.9,squareW30P1D0.99E0.9 -n 30

  def explorationSpace: Map[String, List[Double]] = {
    Map(
      "nbWorkers" -> List(1,10,30),
      "pullingPeriod" -> List(1,5,25),
      "dampening" -> List(0.8, 0.9, 0.95, 0.99),
      "epsilon" -> List(0.3, 0.5, 0.7, 0.9),
    )
  }


  def configs: List[Config] = {
    GridSearch(explorationSpace).run.map(m => {
      defaultConfig
        .withNbWorkers(m("nbWorkers").toInt)
        .withPullingPeriod(m("pullingPeriod").toInt)
        .withDampening(m("dampening"))
        .withEpsilon(m("epsilon"))
    }) ::: List(defaultConfig)
  }

  def getConfig(experimentName: String): Config = configs.filter(_.experimentName == experimentName).head

  def supportedExperimentNames : List[String] = configs.map(_.experimentName)

  def getSquare = {
    getConfig("square")
  }
}

object ConfigFactory extends ConfigFactory