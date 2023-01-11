import main.FileHandler

object FileHandlerTest extends App {

  val test1 = new FileHandler("./config.txt")
  val test2 = new FileHandler("missingConfig.txt")

  assert(test1.creatures.size == 3)
  assert(test2.creatures.isEmpty && test2.fileNotFound)

}
