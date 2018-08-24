class GreedySampler(
                     currentMin: Double, // TODO: add default uniformSamplingInInterval
                     greedyDomainSize: Double,
                     interval: (Double, Double),
                     epsilon: Double,
                     nextRandom: () => Double = math.random,
                   ) {

  val (a, b) = interval // sugar

  def sample() = {
    if (isGreedy) sampleInGreedyDomain
    else sampleInNonGreedyDomain
  }

  private def isGreedy = nextRandom() < epsilon

  private def sampleInGreedyDomain = uniformSamplingInInterval(low, high, nextRandom())
  private def uniformSamplingInInterval(a: Double, b: Double, rand: Double) = rand * (b - a) + a

  private def sampleInNonGreedyDomain = {
    val position: Double = nonGreedyDomainSize * nextRandom()
    if (position < (low - a)) a + position
    else high + (position - (low - a))
  }
  private def nonGreedyDomainSize = (low - a) + (b - high)

  private def low = List(currentMin - (greedyDomainSize / 2), a).max
  private def high = List(currentMin + (greedyDomainSize / 2), b).min

}

object DemoGreedySampler {
  import plotly.{layout, _}
  import Plotly._

  def makeDemo(nbSample: Int) = {
    val sampler = new GreedySampler(0.4, 0.1, (0,1), 0.3)
    val x = (0 until nbSample).map(_ => sampler.sample())
    Histogram(x).plot(title = "Demo GreedySampler (currentMin = 0.4, greedyDomainSize = 0.1, epsilon = 0.3)", xaxis = layout.Axis(title = "Domain"), yaxis = layout.Axis(title = "Number of samples"))
  }

}