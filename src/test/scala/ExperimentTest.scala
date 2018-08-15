import store.{ExperimentData, StoreClient}
import config.Config

class ExperimentTest extends TestHelper {
  trait fixture {
    var store = mock[StoreClient]
    var config = Config(3, 1, shouldStoreValue = true)
    var m = mockFunction[Double]
    def makeStore = (config: Config) => store

    // Default
    store.startExperiment _ expects() once()
    store.endExperiment _ expects() once()

    def withOneLoop = {
      m expects() returns 0
    }

    def setShouldNotStoreValue = {
      config = Config(3, 1, shouldStoreValue = false)
    }

    def withTwoLoops = {
      m expects() returns 1
      m expects() returns 0
    }

  }


  "Experiment.run with one loop" should "call store.StoreValue once" in new fixture {
    withOneLoop
    store.storeValue _ expects 3 once()
    Experiment(config, makeStore, m).run
  }

  "Experiment.run with two loop" should "call store.StoreValue twice" in new fixture {
    withTwoLoops
    store.storeValue _ expects 3 twice()
    Experiment(config, makeStore, m).run
  }

  "Experiment.run with setShouldNotStoreValue" should "call store.StoreValue never" in new fixture {
    withOneLoop
    setShouldNotStoreValue
    store.storeValue _ expects 3 never()
    Experiment(config, makeStore, m).run
  }

}





