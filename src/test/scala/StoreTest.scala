import akka.testkit.TestProbe
import config.ConfigFactory

class StoreTest extends TestHelperWithKit {
  import Store._
  val toDataBase = mockFunction[ExperimentData, Unit]
  val config = ConfigFactory.defaultConfig
  val killSwitchProbe = TestProbe()

  val store = system.actorOf(Store.props(config, killSwitchProbe.ref, toDataBase))

  "!EndExperiment" should "call toDataBase and killSwitch with ShutdownMessage" in {
    toDataBase.expects(*) once()
    store ! StartOfExperiment
    store ! EndOfExperiment(OptimizationResults(List.empty[Observation], Map.empty[String, Int], Observation(0,0)))
    killSwitchProbe.expectMsg(KillSwitch.ShutdownMessage)
  }
}
