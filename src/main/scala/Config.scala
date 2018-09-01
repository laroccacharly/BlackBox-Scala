package config
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
                  experimentName: String = "test_"+math.random()
                 ) {
  def withNbWorkers(nbWorkers: Int): Config = {
    copy(
      nbWorkers = nbWorkers,
      experimentName = experimentName + "W" + nbWorkers.toString
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
    nbWorkers = 10,
    nbIter = 10,
    stoppingCriteria = 0.3,
    pullingPeriod = 100,
    experimentName = "square",
    dampening = 0.8
  )

  def configs: List[Config] = {
    List(
      defaultConfig,
      defaultConfig.withNbWorkers(1)
    )
  }

  def getConfig(experimentName: String): Config = configs.filter(_.experimentName == experimentName).head


  def supportedExperimentNames : List[String] = configs.map(_.experimentName)


  def getSquare = {
    getConfig("square")
  }
}

object ConfigFactory extends ConfigFactory