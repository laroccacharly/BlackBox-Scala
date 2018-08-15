import helpers.Time
import store.ExperimentData
import viewer.Viewer

class ViewerTest extends TestHelper with Time {
  "Viewer" should "call getExperimentData for every fieldValues" in {
    val getExperimentData = mockFunction[String, String, ExperimentData => Unit, Unit]
    val field = "config.experimentName"
    val fieldValues = List("1", "2")
    getExperimentData.expects(field, "1", *)
    getExperimentData.expects(field, "2", *)


    val viewer = new Viewer(field, fieldValues, getExperimentData)
  }
}
