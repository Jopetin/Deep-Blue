package main

import java.io.File
// The core of the program that first uses the file reader to read the initial state and then launches the GUI.
object Simulation extends App {

  val currentLocation = (new File(Simulation.getClass.getProtectionDomain.getCodeSource.getLocation.toURI))
  val filepath = currentLocation.getParent  // the location of the configuration file
  val config = new FileHandler(filepath + "./config.txt")

  // The option structure is used here to determine whether the reading failed or if the file was simply empty.
  val creatures: Option[Vector[CreatureBase]] = if (config.fileNotFound || config.readingError) None else Option(config.creatures.toVector)

  // Creating the GUI object with the read data
  val gui = new GUI(creatures)
  // Start the GUI, the program end when the GUI is shut down.
  gui.main(Array[String]())
}
