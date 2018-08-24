import config.ConfigFactory

object Main extends App {
  override def main(args: Array[String]): Unit = {

    if(args.isEmpty) default
    else {
      args.head match  {
        case "demoSampler" => DemoGreedySampler.makeDemo(1000)
        case s: String => commandNotSupported
      }
    }

    def default = {
      val config = ConfigFactory.makeSquare
      Experiment(config).run
    }

    def commandNotSupported = {
      println("commandNotSupported")
    }
  }
}

