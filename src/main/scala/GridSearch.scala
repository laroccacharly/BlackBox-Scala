/*
  For a given exploration space returns a list of every possible combination of parameters

  Ex:
  Given this map :
  Map("foo" -> List(1,2,3), "bar" -> List(4,5))
  Result :
  List(
    Map("foo" -> 1, "bar" -> 4),
    Map("foo" -> 2, "bar" -> 4),
    Map("foo" -> 3, "bar" -> 4),
    Map("foo" -> 1, "bar" -> 5),
    Map("foo" -> 2, "bar" -> 5),
    Map("foo" -> 3, "bar" -> 5),
  )
 */

package config

case class GridSearch[T](map: Map[String, List[T]]) {

  case class Param[T](name: String, elements: Vector[T], counter: Int = 1) {
    def increaseCounter = copy(counter = counter + 1)
    def nbElements = elements.size
    def lastElement: Boolean = nbElements == counter
    def resetCounter =  copy(counter = 1)
    def currentMapping: Map[String, T] = Map[String,T](name -> elements(counter - 1))
  }

  var params: List[Param[T]] = initParams
  def initParams: List[Param[T]] = map.map(e => Param(e._1, e._2.toVector)).toList


  def run: List[Map[String, T]] = {
    var resultMapping = createMapping :: List.empty[Map[String, T]]

    while(!isDone) {
      increaseCounter
      resultMapping = createMapping :: resultMapping
    }
    resultMapping.reverse
  }

  private def increaseCounter = {
    var counterIncreased = false

    params = params.map(p => {
      var param = p
      if(!p.lastElement && !counterIncreased) {
        param = p.increaseCounter
        counterIncreased = true
      }
      else if(p.lastElement && !counterIncreased) param = p.resetCounter
      else if(counterIncreased) param = p
      else throw new Exception("should not be here")
      param
    })
  }

  private def isDone = params.forall(_.lastElement)

  private def createMapping: Map[String, T] = {
    def iter(params: List[Param[T]], acc: Map[String, T]): Map[String, T] = {
      if (params.isEmpty) acc
      else iter(params.tail, acc ++ params.head.currentMapping)
    }

    iter(params, Map.empty[String,T])
  }
}