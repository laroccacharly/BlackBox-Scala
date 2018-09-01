import config.ConfigFactory

trait Runner extends ConfigFactory with ExperimentRunner with ExperimentViewer {

  def runExperiments(experimentNames: List[String], numberIteration: Int)= {
    (0 until numberIteration).foreach(index => {
      experimentNames.foreach(e => {
        println(s"Running iteration $index of of $numberIteration for experiment $e")
        val config = getConfig(e)
        run(config)
      })
    })
  }

  def cleanDb = DataBaseMD.deleteAll

  def isExperimentNamesValid(experimentNames: List[String]): Boolean = {
    experimentNames.forall(e => supportedExperimentNames.contains(e))
  }

  def isAttributeSupportedByViewer(attribute: String): Boolean = {
    viewableAttributes.contains(attribute)
  }
}
