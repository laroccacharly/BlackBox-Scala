import java.io.ByteArrayOutputStream

class ArgsParserTest extends TestHelper {

  val runExperimentsStub = stubFunction[List[String], Int, Unit]
  val viewExperimentsStub = stubFunction[List[String], String, Unit]
  val cleanDbStub = stubFunction[Unit]

  trait RunnerMock extends Runner {

    override def runExperiments(experimentNames: List[String], numberIteration: Int): Unit =
      runExperimentsStub(experimentNames, numberIteration)

    override def viewExperiments(experimentNames: List[String], attribute: String): Unit =
      viewExperimentsStub(experimentNames, attribute)
    override def cleanDb: Unit = cleanDbStub()


    override def supportedExperimentNames: List[String] = List("square1", "square2")

    override def viewableAttributes: List[String] = List("duration", "accuracy")
  }


  // scopt outputs messages with println. outCapture and errCapture captures them.
  val outCapture = new ByteArrayOutputStream
  val errCapture = new ByteArrayOutputStream

  def processArgs(args: String) = {
    Console.withOut(outCapture) {
      Console.withErr(errCapture) {
        val argsArray = args.split(" ")
        val argsParser = new ArgParser(argsArray) with RunnerMock
        argsParser.execute
      }
    }
  }


  "-e square1 -n 10" should "run experiment square1 10 times" in {
    processArgs("-e square1 -n 10")
    runExperimentsStub.verify(List("square1"), 10)
  }

  "-e square1" should "run experiment square1 1 times" in {
    processArgs("-e square1")
    runExperimentsStub.verify(List("square1"), 1)
  }

  "-e random" should "throw an error because random is not a valid experiment" in {
    assertThrows[IllegalArgumentException](
      processArgs("-e random")
    )
    assert(errCapture.toString.contains("Not a valid experimentNames"))
  }

  "asdf" should "throw an error" in {
    assertThrows[IllegalArgumentException](
      processArgs("asdf")
    )
  }

  "-e square2 -n 5" should "run experiment square2 5 times" in {
    processArgs("-e square2 -n 5")
    runExperimentsStub.verify(List("square2"), 5)
  }


  "--view -e square1 square2 --attribute duration" should "run viewer with square1 and square2 with att duration" in {
    processArgs("view -a duration -e square1,square2")
    viewExperimentsStub.verify(List("square1", "square2"), "duration")
  }

  "view -a fff -e square1,square2" should "throw an error because attribute name is not valid" in {
    assertThrows[IllegalArgumentException](
      processArgs("view -a fff -e square1,square2")
    )
    assert(errCapture.toString.contains("Not a valid attribute"))
  }

  "cleanDb" should "call cleanDb" in {
    processArgs("cleanDb")
    cleanDbStub.verify once()
  }
}
