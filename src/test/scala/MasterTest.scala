import akka.actor.{Actor, ActorRef, Props}
import akka.testkit.{TestActorRef, TestProbe}
import scala.concurrent.duration._



class MasterTest extends TestHelperWithKit {
  import Master._
  import Worker._

  val storeProbe = TestProbe()
  val workerProbe1 = TestProbe("w1")
  val workerProbe2 = TestProbe("w2")

  def firstObservationFromWorker = Observation(0.5, 0.25)
  def secondObservationFromWorker = Observation(0.4, 0.16)
  def initObservation = Observation(1, 1)

  def makeSampler(greedyDomainSize: Double): () => Double = {
    () => 3.1
  }

  def makeMaster: ActorRef = {
    system.actorOf(
      Master.props(
        workers = List(workerProbe1.ref, workerProbe2.ref),
        initObservation = initObservation,
        makeSampler = makeSampler,
        stoppingCriteria = 0.45,
        pullingPeriod = 100,
        store = storeProbe.ref,
        domain = Interval(0,1)
      )
    )
  }

  val master = makeMaster
  When("!Start")
  it should "send Work to all workers and send StartOfExperiment to store" in within(500 millis) {
    master ! Start
    workerProbe1.expectMsg(Work)
    workerProbe2.expectMsg(Work)
    storeProbe.expectMsg(Store.StartOfExperiment)
  }

  When("master receives UpdateBestObservation and no ObservationMessage where received")
  it should "should not send any messages" in within(500 millis) {
    master ! UpdateBestObservation
    workerProbe1.expectNoMessage(100 millis)
    workerProbe2.expectNoMessage(100 millis)
    storeProbe.expectNoMessage(100 millis)
  }

  Given("worker2 sends ObservationMessage to master and stoppingCriteria not reached")
  When("master receives UpdateBestObservation")
  it should "should send Work to worker2 and send UpdateSampler to all workers " in within(500 millis) {
    workerProbe2.send(master, ObservationMessage(firstObservationFromWorker))
    master ! UpdateBestObservation
    workerProbe1.expectMsgType[UpdateSampler]
    workerProbe2.expectMsgType[UpdateSampler]
    workerProbe2.expectMsg(Work)
  }

  Given("worker1 sends ObservationMessage to master and stoppingCriteria is reached")
  When("master receives UpdateBestObservation")
  it should "send EndOfExperiment store with an OptimizationResults" in within(500 millis) {
    workerProbe1.send(master, ObservationMessage(secondObservationFromWorker))
    master ! UpdateBestObservation
    val expectedOptimizationResults = OptimizationResults(List(secondObservationFromWorker, firstObservationFromWorker), Map("w1" -> 1, "w2" -> 1), secondObservationFromWorker)
    storeProbe.expectMsg(Store.EndOfExperiment(expectedOptimizationResults))
    workerProbe1.expectMsgType[UpdateSampler]
    workerProbe2.expectMsgType[UpdateSampler]
  }

  Given("worker1 sends ObservationMessage to master and results are already published")
  When("master receives UpdateBestObservation")
  it should "not send any message" in within(500 millis) {
    workerProbe1.send(master, ObservationMessage(secondObservationFromWorker))
    master ! UpdateBestObservation
    storeProbe.expectNoMessage(100 millis)
    workerProbe1.expectNoMessage(100 millis)
    storeProbe.expectNoMessage(100 millis)
  }
}

