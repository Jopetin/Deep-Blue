package main

import java.io._
import scala.collection.mutable.Buffer

// A basic filereader meant to read the initial creature positions from a file.
// Read data is stored into the reader and can then be accessed to find the results.
// The file format is explained in the config file.
class FileHandler (val filename: String) {

  var creatures = Buffer[CreatureBase]()
  var fileNotFound = false
  var readingError = false

  try {
    val fileReader = new FileReader(filename)
    val lineReader = new BufferedReader(fileReader)
    var currentLine = lineReader.readLine()

    while (currentLine != null) {
      if (!currentLine.isBlank && currentLine.head == '#') {
        currentLine.tail match {
          case "TUNA" =>
            while ({currentLine = lineReader.readLine(); currentLine != null && currentLine.head != '#'}) {
              val parts = currentLine.split(':').map(_.trim)
              if (parts.length == 3 && parts.forall(_.forall(_.isDigit))) {
                creatures += CreatureBase("tuna", parts(0).toInt, parts(1).toInt, Vec.fromDeg(parts(2).toInt))
              }
            }
        case _ => // Unknown config command, do nothing
        }
      }
      currentLine = lineReader.readLine()
    }
    lineReader.close()
    fileReader.close()
  } catch {
    case e: FileNotFoundException => fileNotFound = true
    case e: IOException => readingError = true
  }

}
