import main.Vec
import scala.collection.mutable._

object VecTest extends App {

  var vectors = Buffer[Vec]()
  vectors += Vec(0.1,0.10001)
  vectors += Vec(1,-1)
  vectors += Vec(-0.1, -0.1)
  vectors += Vec(-0.1, 0.1)
  vectors.foreach(v => println(s"Vector (${v.xDir},${v.yDir}) has an orientation of ${v.toDeg} degrees"))

}
