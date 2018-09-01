import Master.{Start, UpdateBestObservation}
import akka.actor.{ActorRef, ActorSystem, Props}
import config.Config

import scala.concurrent.Await
import scala.concurrent.duration._


trait ExperimentRunner {
  def run(config: Config) = Experiment(config).run
}

case class Experiment(config: Config) {
  import  config._
  
  // functions cannot be stored in a DB. So we use a switch statement.
  val f: Double => Double = functionName match {
    case "square" => x => (x - 1) * (x - 1)
  }

  // Initializations
  val domain = Interval(domainMin, domainMax)
  val firstObservation = {
    val firstInputValue = domain.sample
    Observation(firstInputValue, f(firstInputValue))
  }

  val system = ActorSystem()
  val killSwitch = makeKillSwitch
  val workers: List[ActorRef] = makeWorkers
  val store = makeStore
  val master = makeMaster
  val scheduler = system.scheduler

  def run = {
    master ! Start
    val cancellable = setUpScheduler
    Await.ready(system.whenTerminated, 25 seconds)
    cancellable.cancel()
  }


  private def setUpScheduler = {
    import system.dispatcher
    scheduler.schedule(0 milliseconds, pullingPeriod milliseconds, master, UpdateBestObservation)
  }

  private def makeKillSwitch = system.actorOf(KillSwitch.props)
  private def makeStore = system.actorOf(Store.props(config, killSwitch))

  private def makeMaster = system.actorOf(Master.props(workers, stoppingCriteria, firstObservation, makeSampler, pullingPeriod, store, domain, dampening))

  private def makeSampler(greedyDomainSize: Double): () => Double = {
    val sampler = new GreedySampler(currentMin = firstObservation.input, greedyDomainSize, (domain.min, domain.max), epsilon)
    sampler.sample
  }

  private def makeWorkers = {
    def makeWorker(name: String) = system.actorOf(Worker.props(nbIter, makeSampler(domain.size), f), s"$name")
    (0 until nbWorkers).map(n => makeWorker(s"worker$n")).toList
  }
}
