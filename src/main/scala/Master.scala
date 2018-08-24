import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging

object Master {
  case object Start
  case class ObservationMessage(observation: Observation)
  case object UpdateBestObservation
  def props(workers: List[ActorRef],
            stoppingCriteria: Double,
            initObservation: Observation,
            makeSampler: Double => () => Double,
            pullingPeriod: Int,
            store: ActorRef, domain: Interval) =
    Props(new Master(workers, stoppingCriteria, initObservation, makeSampler, pullingPeriod, store, domain))
}


class Master(workers: List[ActorRef],
             stoppingCriteria: Double,
             initObservation: Observation,
             makeSampler: Double => () => Double,
             pullingPeriod: Int,
             store: ActorRef, domain: Interval) extends Actor {
  import scala.collection.mutable.Map
  import Master._
  import Worker._
  import Store._

  val log = Logging(context.system, this)

  var observationActorMapping: Map[ActorRef, Observation] = Map.empty[ActorRef, Observation]
  var observations: List[Observation] = List.empty[Observation]
  val nbObservationsPerWorker: Map[String, Int] = {
    val map = Map.empty[String, Int]
    workers.foreach(a => map(actorName(a)) = 0) // Init at 0 for every worker
    map
  }

  var currentBestObservation = initObservation
  var lastProgress: Double = domain.size

  def receive = {
    case Start =>
      startAllWorkers
      store ! StartOfExperiment
      log.warning("startAllWorkers")


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
          log.warning("publishResults")
          publishResults
        }
        else sendWork

        clearObservationActorMapping
      }
  }

  def actorName(actor: ActorRef): String = actor.path.name.split("-").head

  def stoppingCriteriaReached = lastProgress <= stoppingCriteria

  def receivedObservations = !observationActorMapping.isEmpty

  def storeObservation(actor: ActorRef, observation: Observation) = {
    observationActorMapping(actor) = observation
    observations = observation :: observations
    nbObservationsPerWorker(actorName(actor)) += 1
  }

  def clearObservationActorMapping = observationActorMapping = Map.empty[ActorRef, Observation]

  def publishResults = store ! EndOfExperiment(OptimizationResults(observations, nbObservationsPerWorker.toMap, currentBestObservation))

  def getBestObservationReceivedFromWorkers = observationActorMapping.map(_._2).minBy(_.output)

  def sendWork = observationActorMapping.foreach(_._1 ! Work)

  def startAllWorkers = workers.foreach(worker => worker ! Work)

  def updateBestObservation(observation: Observation) = {
    val newProgress = math.abs(observation.input - currentBestObservation.input)
    lastProgress = (newProgress + lastProgress) / 2
    log.warning(s"lastProgress: $lastProgress")

    currentBestObservation = observation
    log.warning(s"currentBestObservation: $currentBestObservation")

  }
  def stoppingCriteriaNotReached = stoppingCriteria <= lastProgress

  def updateGreedySampler = {
    val sampler = makeSampler(lastProgress)
    workers.foreach(worker => worker ! UpdateSampler(sampler))
  }
}
