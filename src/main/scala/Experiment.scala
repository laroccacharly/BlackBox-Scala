import Master.{Start, UpdateBestObservation}
import akka.actor.{ActorRef, ActorSystem}
import config.Config
import scala.concurrent.duration._

case class Experiment(config: Config) {
  import  config._

  // functions cannot be stored in a DB. So we use a switch statement.
  val f: Double => Double = functionName match {
    case "square" => x => x * x
    case _ => x => x * x
  }

  // Initializations
  val domain = Interval(domainMin, domainMax)
  val firstObservation = {
    val firstInputValue = domain.sample
    Observation(firstInputValue, f(firstInputValue))
  }

  val system = ActorSystem()
  val workers: List[ActorRef] = makeWorkers
  val store = makeStore
  val master = makeMaster
  val scheduler = system.scheduler

  def run = {
    master ! Start
    setUpScheduler
  }


  private def setUpScheduler = {
    import system.dispatcher
    scheduler.schedule(0 milliseconds, pullingPeriod milliseconds, master, UpdateBestObservation)
  }

  private def makeStore = system.actorOf(Store.props(config))

  private def makeMaster = system.actorOf(Master.props(workers, stoppingCriteria, firstObservation, makeSampler, pullingPeriod, store, domain))

  private def makeSampler(greedyDomainSize: Double): () => Double = {
    val sampler = new GreedySampler(currentMin = firstObservation.input, greedyDomainSize, (domain.min, domain.max), epsilon)
    sampler.sample
  }

  private def makeWorkers = {
    def makeWorker(name: String) = system.actorOf(Worker.props(nbIter, makeSampler(domain.size), f), s"$name")
    (0 until nbWorkers).map(n => makeWorker(s"worker$n")).toList
  }
}
