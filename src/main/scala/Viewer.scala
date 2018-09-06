import plotly.element._
import plotly.{element, layout, _}
import Plotly._

import scala.collection.immutable.List
import scala.concurrent._
import scala.concurrent.duration._


trait ExperimentViewer {
  def viewableAttributes = List("executionTime", "accuracy", "AccVsTime")

  def viewExperiments(experimentNames: List[String], attribute: String) {
    Viewer(experimentNames, attribute).viewExperiments
  }
}

case class Viewer(experimentNames: List[String],
                  attribute: String,
                  nbBins: Int = 10,
                  awaitTime: Duration = 10 seconds,
                  loadExperimentData: (String) => Future[List[ExperimentData]] = DataBaseMD.withExperimentData(_)) {


  def viewExperiments = {
    val experimentCollections: List[List[ExperimentData]] = loadData
    val traces = experimentCollections.map(makeTrace)
    makePlot(traces)
  }

  private def makeTrace(data: List[ExperimentData]): Trace = {
    val experimentName = data.head.name

    attribute match {
      case "executionTime" => makeHistogram(data.map(_.executionTime), experimentName)
      case "accuracy" => makeHistogram(data.map(_.accuracy), experimentName)
      case "AccVsTime" => Scatter(data.map(_.executionTime), data.map(_.accuracy), name = experimentName, mode = ScatterMode(ScatterMode.Markers))
    }
  }

  private def loadData = {
    experimentNames.map(e => {
      Await.result(loadExperimentData(e), awaitTime)
    })
  }

  private def makeBins(data: List[Double]) = Bins(data.min, data.max, (data.max - data.min) / nbBins)
  private def makeHistogram(data: List[Double], experimentName: String) =  Histogram(data, name = experimentName, xbins = makeBins(data))

  private def makePlot(trace: Trace) = trace.plot()
  private def makePlot(traces: Seq[Trace]) = traces.plot()
}

