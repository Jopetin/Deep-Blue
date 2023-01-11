package main

import scalafx.scene.paint.Color._

// A Tuna that acts as a flock, grouping together and wandering around.
class Tuna(x: Double,
           y: Double,
           var dir: Vec,
           var maxForce: Double,
           var maxSpeed: Double,
           var viewDistance: Double,
           var separationDistance: Double,
           var cohesionParam: Double,
           var alignmentParam: Double) extends Creature {

  val name = "Tuna"
  var pos = new Position(x, y)


  Seq(0,0,0,10,15,5).foreach(points.add(_))   // creating the base triangle from points (0, 0), (0, 10) and (15, 5)
  layoutX = pos.x   // actually positioning the triangle
  layoutY = pos.y
  fill = Blue
  rotate = dir.toDeg

  // calculating new values for the tuna, separated from the values that affect graphical positions
  def updatePosition(height: Double, width: Double, others: Vector[Creature]) = {
    val acceleration = separation(others).add(cohesion(others)).add(alignment(others)).limitLength(maxForce).add(avoidBorder(height, width))
    dir.add(acceleration).limitLength(maxSpeed)
    this.pos.addVector(dir).limit(height, width, 30)   // limit to not allow tuna to drop out of the screen because of resizing
  }

  // updating the graphical/visual representation to match that of the actual values
  def updateGUI() = {
    this.layoutX = pos.x
    this.layoutY = pos.y
    this.rotate = dir.toDeg
  }

  // Tuna try to keep a distance at least equal to the separationParam to each other
  def separation(others: Vector[Creature]) = {
    var resVec = Vec(0,0)
    // we only need the positions and distances, and take the ones that are not too close or far away
    val neighbours = others.map(n => (this.pos.distance(n.pos), n.pos)).filter(n => n._1 < separationDistance && n._1 > 0)
    if (neighbours.nonEmpty) {
      // the separation force starts at 1 at the separation distance and follows the ratio (separation distance / distance), increasing as distance gets smaller
      neighbours.foreach(n => resVec.add(n._2.diffVector(this.pos).multiply(separationDistance / n._1 / n._1)))
    }
    resVec
  }

  // Tuna try to form groups by attempting to reach the local center of mass
  def cohesion(others: Vector[Creature]) = {
    var resVec = Vec(0,0)
    // we only need the positions and distances, and take the ones that are not too close or far away
    val neighbours = others.map(n => (this.pos.distance(n.pos), n.pos)).filter(n => n._1 < viewDistance && n._1 > 0)
    if (neighbours.nonEmpty) {
      val centerOfMassX = neighbours.foldLeft(0.0)((k, n) => k + n._2.x) / neighbours.size
      val centerOfMassY = neighbours.foldLeft(0.0)((k, n) => k + n._2.y) / neighbours.size
      resVec.add(this.pos.diffVector(Position(centerOfMassX, centerOfMassY)))
    }
    resVec.limitLength(cohesionParam)
  }

  // Tuna try to align into the same direction as their local neighbours
  def alignment(others: Vector[Creature]) = {
    var resVec = Vec(0,0)
    // we only need the positions and velocities, and take the ones that are not too close or far away
    val neighbours = others.map(n => (this.pos.distance(n.pos), n.dir)).filter(n => n._1 < viewDistance && n._1 > 0)
    if (neighbours.nonEmpty) {
      neighbours.foreach(n => resVec.add(n._2))
    }
    resVec.limitLength(alignmentParam)
  }

}
