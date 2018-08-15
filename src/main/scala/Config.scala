package config

// Configuration class to control the behavior of the program

case class Config(difficulty: Int,
                  conditionProbability: Double,
                  experimentName: String = "test_"+math.random(),
                  shouldStoreValue: Boolean = false
                 )


object ConfigFactory {


  def makeConfigs: List[Config] = {
    val difficulty = 10000

    List(
      Config(difficulty, 1e-6, "un"),
      Config(difficulty, 1e-7, "deux"),
      Config(difficulty, 1e-8, "trois")
    )
  }
}