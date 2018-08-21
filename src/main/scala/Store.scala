object Store {
  case object StartOfExperiment
  case class EndOfExperiment(optimizationResults: OptimizationResults)
}

class Store {

}
