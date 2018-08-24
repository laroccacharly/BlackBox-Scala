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
                  experimentName: String = "test_"+math.random()
                 )

object ConfigFactory {
  def makeSquare = {
    Config(
      domainMin = 0,
      domainMax = 1,
      goal = 0,
      functionName = "square",
      epsilon = 0.5,
      nbWorkers = 10,
      nbIter = 10,
      stoppingCriteria = 0.1,
      pullingPeriod = 100,
      experimentName = "square"
    )
  }
}