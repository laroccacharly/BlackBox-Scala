import plotly._
import element._
import layout._
import Plotly._
import algorithm.SimpleAlgorithm
import challenge.SquareChallenge
import plotly.element.Error.Data


val algo_1 = new SimpleAlgorithm(new SquareChallenge, batch_size = 100)
val algo_2 = new SimpleAlgorithm(new SquareChallenge, batch_size = 50)

val algos = List(algo_1, algo_2)

val names = List("1", "2")
val (values, err) = algos.map(a => a.meanAndErrScores).unzip

def make_plot(names: List[String], values: List[Double], errors: List[Double] = null) = {
  val plot = Bar(names, values, error_y = Data(errors))
  plot.plot(title = "Curves")
}

make_plot(names, values, err)