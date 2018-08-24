import akka.actor.{Actor, ActorRef, Props}


object Worker {
  case class UpdateSampler(sampler: () => Double)
  case object Work
  def props(nbIter: Int, makeSample: () => Double, f: Double => Double): Props =
    Props(new Worker(nbIter, makeSample, f))
}

class Worker(nbIter: Int, makeSample: () => Double, f: Double => Double) extends Actor {
  import Worker._
  import Master._

  var sampler: () => Double = makeSample

  override def receive = {
    case UpdateSampler(s) => sampler = s
    case Work => {
      sender ! ObservationMessage(findMin)
    }

  }

  def findMin: Observation = {
    val results = (0 until nbIter).map(_ => {
      val sample = sampler()
      Observation(sample, f(sample))
    })

    results.minBy(observation => observation.output) // Sort by f(sample)
  }

}
