object Main extends App {
  override def main(args: Array[String]): Unit = {
    val argParser = new ArgParser(args)
    argParser.execute
  }
}

