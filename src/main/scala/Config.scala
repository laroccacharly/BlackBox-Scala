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
                  dampening: Double,
                  experimentName: String = "test_"+math.random()
                 )

object ConfigFactory {
  def makeSquare = {
    Config(
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
      dampening = 0.8 // dampening * newProgress + (1 - dampening) * lastProgress
    )
  }
}