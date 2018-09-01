import config._

class ConfigFactoryTest extends TestHelper {

  trait ConfigFactoryMock extends ConfigFactory {
    override def defaultConfig: Config = super.defaultConfig.copy(experimentName = "foo")
  }

  "#getConfig(square)" should "return a config with experimentName square" in new ConfigFactoryMock {
    val config = getConfig("foo")
    assert(config.experimentName == "foo")
  }

  "#withNbWorkers 1" should "return a config with one worker and updated experimentName" in new ConfigFactoryMock {
    var config = getConfig("foo")
    config = config.withNbWorkers(1)
    assert(config.experimentName ==  "fooW1")
    assert(config.nbWorkers ==  1)
  }
}
