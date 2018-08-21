import akka.actor._
import akka.testkit._

class WorkerTest extends TestHelperWithKit{
  import Worker._
  trait fixture {

    val probe = TestProbe("heeo")

    def makeWorker = {
      val props = Worker.props(
        nbIter = 10,
        makeSample = {
          // mock the function
          val sampler = mockFunction[Double]
          (0 until 10).foreach(v => sampler.expects().returns(v).anyNumberOfTimes)
          sampler
        },
        f = {
          val func = mockFunction[Double, Double]
          (10 until 20).foreach(v => func.expects(*).returns(v).anyNumberOfTimes)
          func
        },
        parent = probe.ref
      )
      system.actorOf(props)
    }

    def makeNewSampler = {
      val sampler = mockFunction[Double]
      (100 until 110).foreach(v => sampler.expects().returns(v))
      sampler
    }

  }

  "!Work" should "send observation to its parent" in new fixture {
    val worker =  makeWorker
    worker ! Work
    probe.expectMsg(Observation(0, 10))
  }

  "!UpdateSampler" should "update var sampler" in new fixture {
    val worker = makeWorker
    val newSampler = makeNewSampler
    worker ! UpdateSampler(newSampler)
    worker! Work
    probe.expectMsg(Observation(100, 10))
  }

}

