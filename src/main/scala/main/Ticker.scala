package main

import javafx.animation.AnimationTimer

// A basic animation timer that repeatedly calls the given method
class Ticker(f: () => Unit) extends AnimationTimer {
    override def handle(now: Long): Unit = {f()}
}
