import akka.actor.{Actor, Props}
import akka.event.Logging


object KillSwitch {
  object ShutdownMessage
  def props : Props = Props[KillSwitch]
}

class KillSwitch extends Actor {
  import KillSwitch._
  val log = Logging(context.system, this)

  def receive = {
    case ShutdownMessage => {
      log.warning("Terminating system")
      context.system.terminate()
    }
    case _ => {}
  }
}
