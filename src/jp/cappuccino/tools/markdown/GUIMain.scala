package jp.cappuccino.tools.markdown

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.Pane
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.stage.Modality
import javafx.stage.WindowEvent

import concurrent.future
import concurrent.ExecutionContext.Implicits.global

import jfx.EventProc
import jfx.FXMLUtils
import jfx.InitializableController
import jfx.PaneApplication

import util.ClassUtils
import util.FileUtils

import jp.cappuccino.tools.wkhtml2pdf.WKHTML2PDF




import javafx.collections.ObservableList
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioMenuItem




object GUIMain extends App {
  javafx.application.Application.launch(classOf[MarkdownApplication], args: _*)
}


class MarkdownApplication extends PaneApplication(
  "Caffè Markdown",
  "jp/cappuccino/tools/markdown/res/markdown.fxml",
  new MarkdownController
)


class MarkdownController extends InitializableController {

  import Main.stylesHome

  // FXML resouce name.
  final val AboutFXML = "jp/cappuccino/tools/markdown/res/about.fxml"

  // Preference names.
  final val CurrentDir = "currentDir"
  final val WindowX = "windowX"
  final val WindowY = "windowY"
  final val WindowWidth = "windoWidth"
  final val WindowHeight = "windowHeight"
  final val OutputPDF = "outputPDF"
  final val StyleName = "styleName"
  final val WkHtml2Pdf = "wkhtmltopdf"

  // GUI 
  lazy val _styleChoice = choiceBox[String]("styleChoice")
  lazy val _previewView = webView("previewView")
  lazy val _mainMenu = menuBar("mainMenu")
  lazy val _settingsMenu = find(_mainMenu.getMenus, "settingsMenu")
  lazy val _outputPDFRadio =
    find(_settingsMenu.getItems, "outputToPDFRadio").
    asInstanceOf[RadioMenuItem]
  lazy val _logArea = {
    val log = textArea("logArea")
    log
  }

  def find(list: ObservableList[MenuItem], id: String): MenuItem = {
    val itr = list.iterator
    while (itr.hasNext) {
      val item = itr.next
      if (item.getId == id)
        return item
    }
    null
  }

  def find(list: ObservableList[Menu], id: String): Menu = {
    val itr = list.iterator
    while (itr.hasNext) {
      val item = itr.next
      if (item.getId == id)
        return item
    }
    null
  }

  lazy val fileChooser = {
    val fileChooser = new FileChooser
    fileChooser.setTitle("Choose Markdown file")
    val filters = fileChooser.getExtensionFilters
    filters.setAll(
      new FileChooser.ExtensionFilter("Markdown", "*.md"),
      new FileChooser.ExtensionFilter("All", "*"))
    fileChooser
  }

  lazy val wkhtml2pdfFileChooser = {
    val fileChooser = new FileChooser
    fileChooser.setTitle("Choose wkhtmltopdf execution file")
    val filters = fileChooser.getExtensionFilters
    filters.setAll(
      new FileChooser.ExtensionFilter("wkhtmltopdf", "wkhtmltopdf*"),
      new FileChooser.ExtensionFilter("All", "*"))
    fileChooser
  }

  lazy val helpDialog = {
    val pane = FXMLUtils.load[Pane](AboutFXML, this)
    val scene = new Scene(pane)
    val dialog = new Stage
    dialog.initOwner(stage)
    dialog.initModality(Modality.WINDOW_MODAL)
    dialog.setScene(scene)
    dialog.setTitle("About")
    dialog.setIconified(false)
    dialog.setResizable(false)
    dialog
  }

  def fileOpen: java.io.File = {
    if (currentDir == null || currentDir.isFile || !currentDir.exists) {
      currentDir = new java.io.File(System.getProperty("user.dir"))
    }
    fileChooser.setInitialDirectory(currentDir)
    val file = fileChooser.showOpenDialog(stage)
    if (file != null) {
      currentDir = file.getParentFile
    }
    file
  }

  def currentDir: java.io.File = {
    val dir = prefs.get(CurrentDir, System.getProperty("user.dir"))
    new java.io.File(dir)
  }

  def currentDir_=(dir: java.io.File) {
    prefs.put(CurrentDir, dir.getPath)
  }

  def prefs = java.util.prefs.Preferences.userNodeForPackage(getClass)

  def init {
println("tab pane: " + pane.lookup("#mainTabPane"))
    // Window position.
    val stageX = prefs.getDouble(WindowX, Double.NaN)
    val stageY = prefs.getDouble(WindowY, Double.NaN)
    val stageWidth = prefs.getDouble(WindowWidth, Double.NaN)
    val stageHeight = prefs.getDouble(WindowHeight, Double.NaN)
    if (stageX != Double.NaN && stageY != Double.NaN) {
      stage.setX(stageX)
      stage.setY(stageY)
    }
    if (stageWidth != Double.NaN && stageHeight != Double.NaN) {
      stage.setWidth(stageWidth)
      stage.setHeight(stageHeight)
    }
    // Output to PDF?
    _outputPDFRadio.setSelected(prefs.getBoolean(OutputPDF, false))
    // ChoiceBox styles.
    val styleNames = Style.names(stylesHome.getPath)
    _styleChoice.getItems.setAll(styleNames.toArray: _*)
    val styleName = prefs.get(StyleName, "")
    if (styleName != "")
      _styleChoice.setValue(styleName)
    else
      _styleChoice.setValue(Style.Default)
    // Close event.
    stage.addEventHandler(
      WindowEvent.WINDOW_CLOSE_REQUEST,
      EventProc[WindowEvent] {event =>
        event.getEventType match {
          case WindowEvent.WINDOW_CLOSE_REQUEST =>
            // Save window position.
            prefs.putDouble(WindowX, stage.getX)
            prefs.putDouble(WindowY, stage.getY)
            prefs.putDouble(WindowWidth, stage.getWidth)
            prefs.putDouble(WindowHeight, stage.getHeight)
            // Output to PDF.
            prefs.putBoolean(OutputPDF, _outputPDFRadio.isSelected)
            // Save style name.
            prefs.put(StyleName, _styleChoice.getValue)
          case _ =>
        }
      }
    )
  }

  def handleDragOver(event: DragEvent) {
    val db = event.getDragboard
    if (db.hasFiles) {
      event.acceptTransferModes(TransferMode.COPY, TransferMode.MOVE)
    }
    event.consume
  }

  def handlerDropped(event: DragEvent) {
    val db = event.getDragboard
    val success = 
      if (db.hasFiles) {
        val list = db.getFiles
        future {
          //
          val itr = list.iterator
          while (itr.hasNext) {
            try {
              generateMarkdown(itr.next)
            } catch {
              case exp: java.io.IOException =>
            }
          }
        }
        true
      } else false

    event.setDropCompleted(success)
    event.consume
  }

  def handleFileOpen(event: ActionEvent): Unit =
    future {
      val inpFile = fileOpen
      if (inpFile != null)
        try {
          generateMarkdown(inpFile)
        } catch {
          case exp: Exception =>
            exp.printStackTrace
        }
    }

  def handleFileExit(event: ActionEvent): Unit = {
    Platform.exit
  }

  def handleSettingsWkhtml2pdf(event: ActionEvent): Unit = {
    val inpFile = wkhtml2pdfFileChooser.showOpenDialog(stage)
    if (inpFile != null)
      prefs.put(WkHtml2Pdf, inpFile.getPath)
  }

  def handleClearLog(event: ActionEvent): Unit = {
    _logArea.clear
  }

  def handleHelpAbout(event: ActionEvent) {
    helpDialog.showAndWait()
  }

  def handleHelpAboutClose(event: ActionEvent) {
    helpDialog.close
  }

  private def generateMarkdown(inpFile: java.io.File) {
    try {
      Markdown.generate(stylesHome, _styleChoice.getValue, inpFile)
      log("Success Markdown file")
    } catch {
      case t: Throwable =>
        log("Failed generate Markdown file", t)
    }
    preview(inpFile)
    generatePdf(inpFile)
  }

  private def preview(inpFile: java.io.File): Unit =
    runLater {
      val outpFile = FileUtils.createFile(inpFile.getPath, ".html")
      try {
        val outpURL = outpFile.toURI.toURL
        _previewView.getEngine.load(outpURL.toString)
        log("Success preview")
      } catch {
        case t: Throwable =>
          log("Failed preview", t)
      }
    }

  private def generatePdf(inpFile: java.io.File) {
    val wkhtml2pdfExe = prefs.get(WkHtml2Pdf, null)
    if (wkhtml2pdfExe != null && _outputPDFRadio.isSelected) {
      future {
        val htmlFile = FileUtils.createFile(inpFile.getPath, ".html")
        val pdfFile = FileUtils.createFile(inpFile.getPath, ".pdf")
        try {
          val wkhtml2pdf = new WKHTML2PDF(wkhtml2pdfExe)
//          val baos = new java.io.ByteArrayOutputStream
          wkhtml2pdf.generate(
            htmlFile, pdfFile,
//            {(b: Array[Byte], l: Int) => baos.write(b, 0, l)}
            {(b: Array[Byte], l: Int) =>}
          )
//          log(new String(baos.toByteArray))
          log("Success generate PDF file")
        } catch {
          case t: Throwable =>
            log("Failed generate PDF", t)
        }
      }
    }
  }

  private def log(msg: String): Unit =
    runLater {
      _logArea.appendText(msg)
      _logArea.appendText("\n")
    }

  private def log(msg: String, t: Throwable): Unit =
    runLater {
      _logArea.appendText(msg)
      _logArea.appendText(": Exception: ")
      _logArea.appendText(t.getMessage)
      _logArea.appendText("\n")
    }
}
