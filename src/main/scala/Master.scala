import akka.actor.{Actor, ActorRef, Props}


case class OptimizationResults(observations: List[Observation], nbObservationsPerWorker: Map[String, Int])

object Master {
  case object Start
  case class ObservationMessage(observation: Observation)
  case object UpdateBestObservation
  def props(workers: List[ActorRef],
            stoppingCriteria: Double,
            initObservation: Observation,
            makeSampler: Double => () => Double,
            pullingPeriod: Int,
            store: ActorRef) =
    Props(new Master(workers, stoppingCriteria, initObservation, makeSampler, pullingPeriod, store))
}


class Master(workers: List[ActorRef],
             stoppingCriteria: Double,
             initObservation: Observation,
             makeSampler: Double => () => Double,
             pullingPeriod: Int,
             store: ActorRef) extends Actor {
  import scala.collection.mutable.Map
  import Master._
  import Worker._
  import Store._

  private var observationActorMapping: Map[ActorRef, Observation] = Map.empty[ActorRef, Observation]
  private var observations: List[Observation] = List.empty[Observation]
  private val nbObservationsPerWorker: Map[String, Int] = {
    val map = Map.empty[String, Int]
    workers.foreach(a => map(actorName(a)) = 0) // Init at 0 for every worker
    map
  }

  private def actorName(actor: ActorRef): String = actor.path.name.split("-").head
  private var currentBestObservation = initObservation
  private var lastProgress: Double = Double.PositiveInfinity
  private var nbWaits: Int = 0

  override def receive = {
    case Start =>
      startAllWorkers
      store ! StartOfExperiment

    case ObservationMessage(observation) =>
      storeObservation(sender(), observation)

    case UpdateBestObservation =>
      if (receivedObservations && !stoppingCriteriaReached) {
        val observation = getBestObservationReceivedFromWorkers
        if (observation.output < currentBestObservation.output) {
          updateBestObservation(observation)
          updateGreedySampler
        }

        if (stoppingCriteriaReached) {
          publishResults
        }
        else sendWork

        clearObservationActorMapping
      }
  }

  def stoppingCriteriaReached = lastProgress <= stoppingCriteria

  private def receivedObservations = !observationActorMapping.isEmpty

  private def storeObservation(actor: ActorRef, observation: Observation) = {
    observationActorMapping(actor) = observation
    observations = observation :: observations
    nbObservationsPerWorker(actorName(actor)) += 1
  }

  private def clearObservationActorMapping = observationActorMapping = Map.empty[ActorRef, Observation]

  private def publishResults = store ! EndOfExperiment(OptimizationResults(observations, nbObservationsPerWorker.toMap))

  private def getBestObservationReceivedFromWorkers = observationActorMapping.map(_._2).minBy(_.output)

  private def sendWork = observationActorMapping.foreach(_._1 ! Work)

  private def startAllWorkers = workers.foreach(worker => worker ! Work)

  private def updateBestObservation(observation: Observation) = {
    lastProgress = math.abs(observation.input - currentBestObservation.input)
    currentBestObservation = observation
  }
  private def stoppingCriteriaNotReached = stoppingCriteria <= lastProgress

  private def updateGreedySampler = {
    val sampler = makeSampler(lastProgress)
    workers.foreach(worker => worker ! UpdateSampler(sampler))
  }
}
