import akka.actor.ActorSystem
import akka.testkit.TestKitBase
import org.scalamock.scalatest.MockFactory
import org.scalatest._

trait TestHelper extends FlatSpec
                    with MockFactory
                    with OneInstancePerTest
                    with Matchers
                    with GivenWhenThen
                    with BeforeAndAfterAll {

  trait fixtureHelper {
    def name  = this.getClass.getSimpleName
    def setUpExpectations
    def assertion

    def doTest = {
      Given(name)
      setUpExpectations
      assertion
    }
  }
}

trait TestHelperWithKit extends FlatSpec
                              with MockFactory
                              with Matchers
                              with GivenWhenThen
                              with BeforeAndAfterAll
                              with TestKitBase {
  implicit lazy val system = ActorSystem()

  override def afterAll {
    shutdown(system)
  }
}
