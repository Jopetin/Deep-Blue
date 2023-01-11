package main

import scalafx.application.JFXApp
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Alert, Button, ButtonType, ComboBox, Slider}
import scalafx.scene.layout._
import scalafx.scene.control.Alert._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.collection.mutable.Buffer

class GUI(inputCreatures: Option[Vector[CreatureBase]]) extends JFXApp {

  // the initial and minimum size of the window
  var screenWidth = 800
  var screenHeigth = 800
  val root = new GridPane // GridPane acts as the base for the GUI elements

  stage = new JFXApp.PrimaryStage {
    title.value = "Deep Blue"
    width = screenWidth
    height = screenHeigth
    scene = new Scene(root)
    minWidth = screenWidth
    minHeight = screenHeigth
  }

  // Current tuna behaviour parameters, set to their initial (natural) values
  // These can be changed through the GUI and are used when creating new tuna.
  var tunaMaxForce: Double = 0.12
  var tunaMaxSpeed: Double = 2
  var tunaViewDistance: Double = 50
  var tunaSeparationDistance: Double = 20
  var tunaCohesionParam: Double = 0.1
  var tunaAlignmentParam: Double = 1

  // a pop-up warning to be used if the file reading was unsuccessful
  val fileAlert = new Alert(AlertType.Information) {
    title = "Warning"
    headerText = "Failure in reading the config file, the simulation will launch empty."
    buttonTypes = Seq(ButtonType.OK)
  }

  // inputBase acts as the initial situation for the simulation, which one can revert back to.
  // In case the file reading somehow failed, the user is warned and an empty simulation is booted.
  val inputBase = inputCreatures match {
    case Some(creatures) =>
      creatures
    case None =>
      fileAlert.showAndWait()
      Vector[CreatureBase]()
  }

  // wildlife is the heart of the simulation and holds all of the simulated creatures inside.
  var wildlife = Buffer[Creature]()

  // optionBar is the top portion of the window, housing all of the tools available for the user.
  val optionBar = new HBox
  optionBar.setBackground(new Background(Array(new BackgroundFill(LightGoldrenrodYellow, CornerRadii.Empty, Insets.Empty))))

  // tank is the pane responsible for housing the simulated Creatures
  val tank = new Pane
  tank.setBackground(new Background(Array(new BackgroundFill(LightBlue, CornerRadii.Empty, Insets.Empty))))

  // Add both of the major GUI elements on top of each other into the root
  root.add(optionBar, 0, 0)
  root.add(tank, 0, 1)

  // a button for pausing and playing the simulation. The simulation initally starts paused.
  val pauseButton = new Button()
  pauseButton.minWidth = 60
  pauseButton.setText("Play")
  var pauseMode = true
  pauseButton.onAction = event => {
    if (!pauseMode) {
      pauseButton.setText("Play")
      pauseMode = true
      animation.stop()
    } else {
      pauseButton.setText("Pause")
      pauseMode = false
      animation.start()
    }
  }

  // a button for reverting the simulation into its initial state
  val resetButton = new Button()
  resetButton.minWidth = 60
  resetButton.setText("Reset")
  resetButton.onAction = event => {
    wildlife = initialCreatures()
    tank.children = wildlife
  }

  // An input slider that is used to choose new values for parameters
  val inputSlider = new Slider
  inputSlider.setSnapToTicks(true)
  inputSlider.setShowTickMarks(true)
  inputSlider.setShowTickLabels(true)
  inputSlider.hgrow = Priority.Always
  inputSlider.disable = true

  // a combo/option box from which the user can pick a parameter to change.
  // The current program only supports changing tuna behaviours so this would need to be extended to allow support for multiple creatures.
  // The code here contains a lot of arbitrary seeming numbers, as they are used to limit the changes the user can do using the value slider.
  // These are reasonable values set while considering how the parameters are used within the simulation calculations.
  val valueField = new ComboBox[String] {
    items = ObservableBuffer("Max speed", "Max force", "View distance", "Separation distance", "Cohesion strength", "Alignment strength")
  }
  valueField.onAction = event => {
    selectButton.disable = false
    inputSlider.disable = false
    valueField.getValue match {
      case "Max speed" =>
        inputSlider.setMax(10)
        inputSlider.setMin(0)
        inputSlider.setValue(tunaMaxSpeed)
        inputSlider.setMajorTickUnit(1)
        inputSlider.setMinorTickCount(3)
      case "Max force" =>
        inputSlider.setMax(3)
        inputSlider.setMin(0)
        inputSlider.setValue(tunaMaxForce)
        inputSlider.setMajorTickUnit(0.6)
        inputSlider.setMinorTickCount(4)
      case "View distance" =>
        inputSlider.setMax(300)
        inputSlider.setMin(0)
        inputSlider.setValue(tunaViewDistance)
        inputSlider.setMajorTickUnit(50)
        inputSlider.setMinorTickCount(4)
      case "Separation distance" =>
        inputSlider.setMax(100)
        inputSlider.setMin(0)
        inputSlider.setValue(tunaSeparationDistance)
        inputSlider.setMajorTickUnit(20)
        inputSlider.setMinorTickCount(3)
      case "Cohesion strength" =>
        inputSlider.setMax(3)
        inputSlider.setMin(0)
        inputSlider.setValue(tunaCohesionParam)
        inputSlider.setMajorTickUnit(0.5)
        inputSlider.setMinorTickCount(4)
      case "Alignment strength" =>
        inputSlider.setMax(5)
        inputSlider.setMin(0)
        inputSlider.setValue(tunaAlignmentParam)
        inputSlider.setMajorTickUnit(1)
        inputSlider.setMinorTickCount(3)
    }
  }

  // a button used to confirm the change of a parameters value.
  // It currently only has one layer of matching and only changes values for tunas.
  // It could be extended to also match an another option value and change values for different kinds of creatures.
  val selectButton = new Button("Change parameter value")
  selectButton.disable = true
  selectButton.onAction = event => {
    valueField.getValue match {
      case "Max speed" =>
        tunaMaxSpeed = inputSlider.getValue
        wildlife.filter(_.name == "Tuna").foreach(_.maxSpeed = tunaMaxSpeed)
      case "Max force" =>
        tunaMaxForce = inputSlider.getValue
        wildlife.filter(_.name == "Tuna").foreach(_.maxForce = tunaMaxForce)
      case "View distance" =>
        tunaViewDistance = inputSlider.getValue
        wildlife.filter(_.name == "Tuna").foreach(_.viewDistance = tunaViewDistance)
      case "Separation distance" =>
        tunaSeparationDistance = inputSlider.getValue
        wildlife.filter(_.name == "Tuna").foreach(_.separationDistance = tunaSeparationDistance)
      case "Cohesion strength" =>
        tunaCohesionParam = inputSlider.getValue
        wildlife.filter(_.name == "Tuna").foreach(_.cohesionParam = tunaCohesionParam)
      case "Alignment strength" =>
        tunaAlignmentParam = inputSlider.getValue
        wildlife.filter(_.name == "Tuna").foreach(_.alignmentParam = tunaAlignmentParam)
    }
  }

  // Adding constraints so that both major GUI elements can extend horizontally but only the fish tank extends vertically
  val rowOptionBar = new RowConstraints()
  rowOptionBar.setVgrow(Priority.Never)
  val rowTankBar = new RowConstraints()
  rowTankBar.setVgrow(Priority.Always)
  val columnSettings = new ColumnConstraints()
  columnSettings.setHgrow(Priority.Always)
  root.rowConstraints = Array(rowOptionBar, rowTankBar)
  root.columnConstraints = Array(columnSettings)


  // Add all of the tools to the option bar, the order of adding defines the placement order from left to right
  optionBar.children += pauseButton
  optionBar.children += resetButton
  optionBar.children += valueField
  optionBar.children += selectButton
  optionBar.children += inputSlider

  // Initialize the updateStatus, which is used to track the concurrent simulation progress
  var updateStatus = Future.successful()

  // runs the position calculations on all of the creatures.
  // The calculation is done parallel on its own thread and if the previous calculation is still ongoing, the method does nothing.
  def update() = {
    if (updateStatus.isCompleted) {
      wildlife.foreach(_.updateGUI())
      updateStatus = Future {wildlife.foreach(_.updatePosition(tank.height.value, tank.width.value, wildlife.toVector))}
    }
  }

  // the ticker that is responsible for updating the simulation state
  val animation = new Ticker(update)

  // Creates a tuna using the current behaviour parameters
  def createTuna(x: Double, y: Double, dir: Vec): Tuna = {
    new Tuna(
      x,
      y,
      dir,
      tunaMaxForce,
      tunaMaxSpeed,
      tunaViewDistance,
      tunaSeparationDistance,
      tunaCohesionParam,
      tunaAlignmentParam
    )
  }

  // returns a buffer representing the initial situation
  def initialCreatures(): Buffer[Creature] = {
    val retBuf = Buffer[Creature]()
    for (base <- inputBase) {
      base.name match {
        case "tuna" => retBuf += createTuna(base.x, base.y, base.dir.copy)
        case _ =>
      }
    }
    retBuf
  }

  // initializes the wildlife with the starting scenario and adds its contents to the tank.
  wildlife = initialCreatures()
  tank.children = wildlife

  // Clicks on the tank is the way to add randomly oriented tunas in the middle of the simulation.
  tank.onMouseClicked = click => {
    val spawn = createTuna(click.getX, click.getY, Vec.randomDir(tunaMaxSpeed))
    this.wildlife += spawn
    tank.children = wildlife
  }

}

