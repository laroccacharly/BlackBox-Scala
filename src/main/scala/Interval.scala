case class Interval(min: Double, max: Double) {
  def size: Double = max - min
  def sample: Double  = math.random() * (max - min) + min
}
