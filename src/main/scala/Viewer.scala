import plotly.element.Error.Data
import plotly.{layout, _}
import Plotly._
import helpers.Algebra

import scala.collection.mutable.Map
import scala.collection.immutable.List


class Viewer(field: String,
             fieldValues: List[String],
             getExperimentData: (String, String, ExperimentData => Unit) => Unit = DataBaseMD.getDocuments(_, _, _)) extends Algebra
{
  val immutableStore = fieldValues.map(fieldValue => (fieldValue, List.empty[ExperimentData])).toMap // init the local store
  var store: Map[String, List[ExperimentData]] = Map(immutableStore.toSeq: _*) // convert to mutable Map
  fieldValues.foreach(fieldValue => getExperimentData(field, fieldValue, addToStore(fieldValue))) // import data to the store

  private def addToStore(fieldValue: String)(experimentData: ExperimentData) = {
    val previous: List[ExperimentData] = store(fieldValue)
    store(fieldValue) =  experimentData :: previous
  }


  def makeBarPlot(p: ExperimentData => Double,
                    title: String = "No Title",
                    xaxis: String = field,
                    yaxis: String = "yaxis"
                   ) = {
    val (values, errors) = fieldValues.map( fieldValue => {
      meanAndStd(store(fieldValue).map(experimentData => p(experimentData)))
    }).unzip

    Bar(fieldValues, values, error_y = Data(errors)).plot(title = title, xaxis = layout.Axis(title = xaxis), yaxis = layout.Axis(title = yaxis))
  }
}

