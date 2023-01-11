package main

import scalafx.scene.shape.Polygon

// Depicts a creature that has a certain behaviour and can be used within the simulation.
// The polygon extension allows creatures to be directly drawn into the GUI.
abstract class Creature extends  Polygon {

  val name: String
  var pos: Position
  var dir: Vec
  var maxSpeed: Double
  var maxForce: Double
  var viewDistance: Double
  var separationDistance: Double
  var cohesionParam: Double
  var alignmentParam: Double

  // creatures need to have a method to update its position and orientation based on the behaviour.
  def updatePosition(height: Double, width: Double, others: Vector[Creature]): Unit

  // creatures need to have a separate method to update the visual representation to match the newly calculated values
  def updateGUI(): Unit

  def avoidBorder(height: Double, width: Double): Vec = {    // Avoiding the border of the screen
    val dist = 40   // reaction distance from the border of the screen
    var resVec = Vec(0,0)
    val orientation = this.dir.toDeg

    // The result Vec's are multiplied by two with the left and right wall so that they have priority over top and bottom.
    // The reason for this is that for example top and right collisions don't override each other.
    // The power is also related to the max force of the fish so that it's always strong enough to oppose other forces.

    if (this.pos.x < dist) {    // left border
      if (orientation <= 180 && orientation >= 90) {
        resVec.add(Vec(maxForce, maxForce).multiply(2))
      } else if (orientation >= 180 && orientation <= 270) {
        resVec.add(Vec(maxForce, -maxForce).multiply(2))
      }
    }
    if (this.pos.x > width - dist) {    // right border
      if (orientation <= 90 && orientation >= 0) {
        resVec.add(Vec(-maxForce, maxForce).multiply(2))
      } else if (orientation >= 270 && orientation <= 360) {
        resVec.add(Vec(-maxForce, -maxForce).multiply(2))
      }
    }
    if (this.pos.y < dist) {    // top border
      if (orientation <= 270 && orientation >= 180) {
        resVec.add(Vec(-maxForce, maxForce))
      } else if (orientation >= 270 && orientation <= 360) {
        resVec.add(Vec(maxForce, maxForce))
      }
    }
    if (this.pos.y > height - dist) {   // bottom border
      if (orientation <= 90 && orientation >= 0) {
        resVec.add(Vec(maxForce, -maxForce))
      } else if (orientation >= 90 && orientation <= 180) {
        resVec.add(Vec(-maxForce, -maxForce))
      }
    }
    resVec
  }

  override def toString(): String = "a Creature in " + pos.x.toString + " , " + pos.y.toString + " going towards " + dir

}
