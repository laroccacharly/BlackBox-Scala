/*
  Runner combines traits. Used by the ArgParser
 */

import config.ConfigFactory

trait Runner extends ConfigFactory with ExperimentRunner with ExperimentViewer {

  def runExperiments(experimentNames: List[String], numberIteration: Int, verbose: Boolean)= {
    experimentNames.foreach(e => {
      val config = getConfig(e)
      (0 until numberIteration).foreach(index => {
        println(s"Running iteration $index of of $numberIteration for experiment $e")
        println("Config :" + config.toString)
        run(config, verbose)
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
