class GreedySamplerTest extends TestHelper {

  trait basicCase extends fixtureHelper {
    // Parameters
    def currentMin: Double = 3
    def greedyDomainSize: Double = 1
    def interval: (Double, Double) = (0, 10)
    def epsilon = 0.5


    // Function Mocking
    val nextRandom = mockFunction[Double]
    def randomNumbers = List(0.6, 0.3)
    def setUpExpectations = randomNumbers.foreach(n => nextRandom.expects().returns(n))

    //Expectation
    def expectedSampleValue = 3.7

    // Make sampler
    def sampler = new GreedySampler(
      currentMin = currentMin,
      greedyDomainSize = greedyDomainSize,
      interval = interval,
      epsilon = epsilon,
      nextRandom = nextRandom
    )

    def assertion = sampler.sample() should be (expectedSampleValue +- 0.1)
  }

  object Case1 extends basicCase
  object Case2 extends basicCase {
    override def randomNumbers = List(0, 0.3)
    override def expectedSampleValue: Double = 2.8
  }
  object Case3 extends basicCase {
    override def randomNumbers = List(0.6, 0.1)
    override def expectedSampleValue: Double = 0.8
  }
  object Case4 extends basicCase {
    override def currentMin = 0.25
    override def randomNumbers = List(0.6, 0)
    override def expectedSampleValue: Double = 0.75
  }
  object Case5 extends basicCase {
    override def currentMin = 0.25
    override def randomNumbers = List(0.6, 1)
    override def expectedSampleValue: Double = 10
  }
  object Case6 extends basicCase {
    override def currentMin = 0.25
    override def randomNumbers = List(0.4, 0)
    override def expectedSampleValue: Double = 0
  }
  object Case7 extends basicCase {
    override def currentMin = 0.25
    override def randomNumbers = List(0.4, 1)
    override def expectedSampleValue: Double = 0.75
  }

  val CasesToTest = List(Case1, Case2, Case3, Case4, Case5, Case6, Case7)
  "#sample" should "return expected results" in {
    CasesToTest.foreach(c => c.doTest)
  }
}
