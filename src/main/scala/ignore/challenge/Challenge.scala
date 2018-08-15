package challenge


abstract class Challenge {
  def f: Double => Double
  val interval: (Double, Double)
  val target: Double
  val tolerance: Double

  var done = false
  private def check_value(value: Double): Boolean = math.abs(target - f(value)) < target * tolerance
  private def uniform(interval: (Double, Double)) = math.random() * (interval._2 - interval._1) + interval._1
  private def next_sample = () => uniform(interval)

  def step = if(!done && check_value(next_sample())) done = true
}

class SquareChallenge extends Challenge {
  override def f: Double => Double = x => (x - 1) * (x -1) + 1
  override val target: Double = 1
  override val tolerance: Double = 0.001
  override val interval: (Double, Double) = (-1, 2)
}
