import akka.actor.{Actor, Props}

object ShutdownMessage

object KillSwitch {
  def props : Props = Props[KillSwitch]
}

class KillSwitch extends Actor {
  def receive = {
    case ShutdownMessage => context.system.terminate()
    case _ => {}
  }
}
