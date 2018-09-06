class ArgParser(args: Array[String]) extends Runner {

  case class ArgsConfig(experimentNames: Seq[String] = Seq(),
                        numberIteration: Int = 1,
                        attribute: String = "",
                        verbose: Boolean = false,
                        mode: String = "main"
                       )

  val parser = new scopt.OptionParser[ArgsConfig]("BlackBox") {
    head("BlackBox", "0.x")
    opt[Seq[String]]('e', "experimentNames")
      .valueName("<exp1>,<exp2>...")
      .action( (x,c) => c.copy(experimentNames = x) )
      .text("experimentNames to process.")
      .validate( names => {

        if (isExperimentNamesValid(names.toList)) success
        else failure("Not a valid experimentNames. Supported experiment names : "
          + supportedExperimentNames.reduce((a,b) => a + "," + b))
      })

    opt[Int]('n', "numberIteration")
      .action( (x,c) => c.copy(numberIteration = x) )
      .text("number of experiment executions")
      .validate( n => {
        if (n >= 1) success
        else failure("Not a valid numberIteration")
      })

    opt[Unit]("verbose")
      .action( (_, c) => c.copy(verbose = true) )
      .text("verbose is a flag")


    cmd("view").
      action( (_, c) => c.copy(mode = "view") ).
      text("view experiments").
      children(
        opt[String]('a', "attribute")
          .action( (x,c) => c.copy(attribute = x) )
          .text("attribute to plot")
          .required
          .validate( a => {
            if (isAttributeSupportedByViewer(a)) success
            else failure("Not a valid attribute. It should either of theses : "
              + viewableAttributes.toString)
          })
      )

    cmd("cleanDb").
      action( (_, c) => c.copy(mode = "cleanDb") ).
      text("clean database")
  }

  def execute = {
    parser.parse(args, ArgsConfig()) match {
      case Some(ArgsConfig(experimentNames, numberIteration, attribute, v, mode)) => {
        mode match {
          case "view" => viewExperiments(experimentNames.toList, attribute)
          case "cleanDb" => cleanDb
          case _ => runExperiments(experimentNames.toList, numberIteration)
        }
      }

      case None =>
        throw new IllegalArgumentException("Arguments are not valid")
    }
  }

}