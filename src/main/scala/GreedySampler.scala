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
    else high + position
  }
  private def nonGreedyDomainSize = (low - a) + (b - high)

  private def low = List(currentMin - (greedyDomainSize / 2), a).max
  private def high = List(currentMin + (greedyDomainSize / 2), b).min

}