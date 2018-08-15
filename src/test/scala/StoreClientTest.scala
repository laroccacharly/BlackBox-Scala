import config.Config
import store.{ExperimentData, StoreClient}

class StoreClientTest extends TestHelper {
  val config = Config(3, 0.1)
  val toDataBase = mockFunction[ExperimentData, Unit]
  val store = new StoreClient(config, toDataBase)

  "After init" should "have empty values array" in {
    assert(store.values.count(_ => true) == 0)
  }

  "StoreClient.storeValue" should "add one to values array" in {
    store.storeValue(3)
    assert(store.values.count(_ => true) == 1)
  }

  "StoreClient.startExperiment" should "set startExperiment to current time" in {
    store.startExperiment
    assert(store.startTime != null)
  }

  "StoreClient.endExperiment" should "set startExperiment to current time and call toDataBase" in {
    toDataBase.expects(*) once()
    store.endExperiment
    assert(store.endTime != null)
  }
}
