import config.{ConfigFactory}


object Main extends App {
  override def main(args: Array[String]): Unit = {

    if(args.isEmpty) default
    else {
      args.head match  {
        case "viewer" => viewer123
        case s: String => coolStuff
      }
    }


    def default = {
      ConfigFactory.makeConfigs.foreach(config => {
        Experiment(config).run
      })
    }

    def coolStuff = {
      println("Fill me up")
    }

    def viewer123 = {
      import viewer._
      val v = new Viewer("config.experimentName", List("un", "deux", "trois"))
      v.makeBarPlot(e => e.executionTime)
    }

  }
}
