package main

// A class meant to store only the essential information about a creature.
// These kinds of objects can be used a placeholder for creatures later to be created
// since the behaviour parameters are stored in the GUI.
case class CreatureBase(name: String, x: Double, y: Double, dir: Vec)
