package main
import scala.math.{sqrt, min, max}

// A class used to track positions in a two dimensional field.
// Has a certain degree of synergy with objects from Vec class.
case class Position(var x: Double, var y: Double) {

  def distance(another: Position): Double = {   // distance between two positions
    sqrt((this.x - another.x) * (this.x - another.x) + (this.y - another.y) * (this.y - another.y))
  }

  def diffVector(another: Position): Vec = {   // a vector pointing from this position to an another
    Vec(another.x - this.x, another.y - this.y)
  }

  def addVector(vector: Vec): Position = {    // a position pointed by a vector from this current position
    this.x += vector.xDir
    this.y += vector.yDir
    this
  }

  // limits the position within given values, in order to prevent tuna from escaping the simulation.
  def limit(height: Double, width: Double, margin: Double): Position = {
    this.x = max(0.0, min(this.x, width - margin))
    this.y = max(0.0, min(this.y, height - margin))
    this
  }


  override def toString = "Position: " + this.x + "," + this.y

}
