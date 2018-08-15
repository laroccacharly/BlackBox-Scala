package helpers
import breeze.linalg._
import breeze.stats._

trait Algebra {
  def meanAndStd(list: List[Double]): (Double, Double) = {
    val scores = DenseVector(list:_*)
    (mean(scores), stddev(scores))
  }
}
