package main

import scala.math.sqrt
import scala.math.{atan, sin, cos}
import scala.util.Random

// A general 2D-vector class
case class Vec(var xDir: Double, var yDir: Double) {

  def add(another: Vec): Vec = {    // Adding two vector together
    this.xDir = this.xDir + another.xDir
    this.yDir = this.yDir + another.yDir
    updateLength()
    this
  }

  // this way the length is only calculated once, when it changes
  var length = sqrt(this.xDir * this.xDir + this.yDir * this.yDir)
  def updateLength() = {this.length = sqrt(this.xDir * this.xDir + this.yDir * this.yDir)}

  def setLength(length: Double): Vec = {    // Forming a unit-vector (length = 1)
    if (this.length > 0) {
      this.xDir = this.xDir / this.length * length
      this.yDir = this.yDir / this.length * length
    } else {
      this.xDir = length
    }
    updateLength()
    this
  }

  def limitLength(len: Double): Vec = {   // Forming a custom length version of a vector
    if (this.length > len && this.length > 0) {
      this.xDir = this.xDir / this.length * len
      this.yDir = this.yDir / this.length * len
    }
    updateLength()
    this
  }

  def copy: Vec = {   // create a copy of an existing vector
    Vec(this.xDir, this.yDir)
  }

  def multiply(multiplier: Double): Vec = {   // vector multiplication
    this.xDir *= multiplier
    this.yDir *= multiplier
    updateLength()
    this
  }

  def toDeg: Double = {    // Converting a speed vector into orientation in clockwise rotation (where Vec(1,0) is equivalent of 0 degrees)
    if (xDir > 0) {
      if (yDir > 0) {
        atan(yDir/xDir).toDegrees
      } else {
        360 + atan(yDir/xDir).toDegrees
      }
    } else if (xDir < 0) {
      180 + atan(yDir/xDir).toDegrees
    } else {
      if (yDir >= 0) {
        90
      } else {
        270
      }
    }
  }

  override def toString: String = "(" + this.xDir + " / " + this.yDir + ")"

}

object Vec {

  def randomDir(length: Double): Vec = {    // create a random vector of given length
    val x: Double = (Random.nextInt(101) - 50)
    val y: Double = (Random.nextInt(101) - 50)
    Vec(x, y).setLength(length)
  }

  def fromDeg(deg: Double): Vec = {    // creates a unit vector pointing to the direction given in degrees.
    var realDeg = deg   // Lets convert the degree value to be inside the 0 to 360 range
    if (realDeg > 360) {
      realDeg = realDeg - (realDeg / 360).toInt * 360
    } else if (realDeg < 0) {
      if (realDeg < -360) {
        realDeg = realDeg - (realDeg / 360).toInt * 360
      }
    }
    Vec(cos(realDeg.toRadians), sin(realDeg.toRadians))
  }

}