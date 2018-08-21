import akka.actor.{Actor, ActorRef, Props}

case class Observation(input: Double, output: Double)
case class Interval(min: Double, max: Double)
case class ProblemDefinition(f: Double => Double, domain: Interval, goal: Double)

object Worker {
  case class UpdateSampler(sampler: () => Double)
  case object Work
  def props(nbIter: Int, makeSample: () => Double, f: Double => Double, parent: ActorRef): Props =
    Props(new Worker(nbIter, makeSample, f, parent))
}

class Worker(nbIter: Int, makeSample: () => Double, f: Double => Double, parent: ActorRef) extends Actor {
  import Worker._
  private var sampler: () => Double = makeSample

  override def receive = {
    case UpdateSampler(s) => sampler = s
    case Work => work
  }

  private def findMin: Observation = {
    val results = (0 until nbIter).map(_ => {
      val sample = sampler()
      Observation(sample, f(sample))
    })

    results.minBy(tuple => tuple.output) // Sort by f(sample)
  }

  private def work = {
    parent ! findMin
  }
}
