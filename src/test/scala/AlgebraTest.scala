import helpers.Algebra

class AlgebraTest extends TestHelper with Algebra {
  "Algebra.meanAndStd" should "return the mean and std" in {
    val (mean, std) = meanAndStd(List(10, 10, 9))
    assert(mean > 9 && mean < 10)
    assert(std > 0 && std < 2)
  }
}