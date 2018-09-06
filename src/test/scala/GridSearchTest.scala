import config._

class GridSearchTest extends TestHelper {

  val map = Map("foo" -> List(1,2,3), "bar" -> List(4,5))
  val expectedResult = List(
    Map("foo" -> 1, "bar" -> 4),
    Map("foo" -> 2, "bar" -> 4),
    Map("foo" -> 3, "bar" -> 4),
    Map("foo" -> 1, "bar" -> 5),
    Map("foo" -> 2, "bar" -> 5),
    Map("foo" -> 3, "bar" -> 5),
  )

  "#run" should "return expected result" in {
    assert(GridSearch(map).run == expectedResult)
  }

}
