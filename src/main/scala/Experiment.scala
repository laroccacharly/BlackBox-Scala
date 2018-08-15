import config.Config
import store._

case class Experiment(config: Config, makeStore: Config => StoreClient = StoreHelper.makeStore, nextRandom: () => Double = math.random) {

  val store = makeStore(config)

  def run = {
    store.startExperiment
    whileloop
    store.endExperiment
  }

  private def whileloop = {
    var done = false
    while(!done) {
      val value = f(nextValue)
      if (config.shouldStoreValue) store.storeValue(value)
      done = processValue(value)
    }
  }

  private def recursiveLoop(done: Boolean = false) {
    if (!done) {
      val value = f(nextValue)
      if (config.shouldStoreValue) store.storeValue(value)
      recursiveLoop(processValue(value))
    }
  }

  private def nextValue = 3
  private def f: Double => Double = _ => (1 until config.difficulty).sum
  private def processValue(value: Double): Boolean = {
    nextRandom() < config.conditionProbability
  }
}