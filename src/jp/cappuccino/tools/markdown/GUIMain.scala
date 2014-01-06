package jp.cappuccino.tools.markdown

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.fxml.FXMLLoader
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.WindowEvent
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.web.WebView



import concurrent.future
import concurrent.ExecutionContext.Implicits.global



object GUIMain extends App {
  javafx.application.Application.launch(classOf[MarkdownApplication], args: _*)
}


class MarkdownApplication extends PaneApplication(
  "Caffè Markdown",
  "jp/cappuccino/tools/markdown/markdown.fxml",
  new MarkdownController
)


class PaneApplication(
  title: String,
  fxmlPath: String,
  controller: InitializableController
) extends javafx.application.Application {

  @throws(classOf[Exception])
  override def start(stage: Stage) {
    val fxmlLoader = new FXMLLoader(ClassUtils.resource(fxmlPath))
    fxmlLoader.setController(controller)
    val pane = fxmlLoader.load.asInstanceOf[Pane]
    controller.init(stage, pane)
    val scene = new Scene(pane)
    stage.setTitle(title)
    stage.setScene(scene)
    stage.show
  }
}


class MarkdownController extends InitializableController {

  import Main.stylesHome

  final val CurrentDir = "currentDir"
  final val WindowX = "windowX"
  final val WindowY = "windowY"
  final val WindowWidth = "windoWidth"
  final val WindowHeight = "windowHeight"
  final val WkHtml2Pdf = "wkhtmltopdf"
  final val AboutFXML = "jp/cappuccino/tools/markdown/about.fxml"

  lazy val _styleChoice = choiceBox[String]("styleChoice")
  lazy val _previewView = webView("previewView")



//  lazy val _tabPane = tabPane("mainTabPane")
//  lazy val _aboutTab = tab("aboutTab")

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
    // ChoiceBox styles.
    val styleNames = Style.names(stylesHome.getPath)
    _styleChoice.getItems.setAll(styleNames.toArray: _*)
    _styleChoice.setValue(Style.Default)
    // Close event.
    stage.addEventHandler(
      WindowEvent.WINDOW_CLOSE_REQUEST,
      new EventHandler[WindowEvent]() {
        def handle(event: WindowEvent) {
          event.getEventType match {
            case WindowEvent.WINDOW_CLOSE_REQUEST =>
              prefs.putDouble(WindowX, stage.getX)
              prefs.putDouble(WindowY, stage.getY)
              prefs.putDouble(WindowWidth, stage.getWidth)
              prefs.putDouble(WindowHeight, stage.getHeight)
            case _ =>
          }
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

  lazy val helpDialog = {
    val fxmlLoader = new FXMLLoader(ClassUtils.resource(AboutFXML))
    fxmlLoader.setController(this)
    val pane = fxmlLoader.load.asInstanceOf[Pane]
    val scene = new Scene(pane)
    val dialog = new Stage
    dialog.initOwner(stage)
    dialog.initModality(Modality.WINDOW_MODAL)
    dialog.setTitle("About")
    dialog.setScene(scene)
    dialog
  }


  def handleHelpAbout(event: ActionEvent) {
    helpDialog.showAndWait()
  }

  def handleHelpAboutClose(event: ActionEvent) {
    helpDialog.close
  }


  private def generateMarkdown(inpFile: java.io.File) {
    Markdown.generate(stylesHome, _styleChoice.getValue, inpFile)
    preview(inpFile)
    generatePdf(inpFile)
  }

  private def preview(inpFile: java.io.File): Unit =
    runLater {
      val outpFile = FileUtils.createFile(inpFile.getPath, ".html")
      val outpURL = outpFile.toURI.toURL
      _previewView.getEngine.load(outpURL.toString)
    }

  private def generatePdf(inpFile: java.io.File) {
    val wkhtml2pdfExe = prefs.get(WkHtml2Pdf, null)
    if (wkhtml2pdfExe != null) {
      future {
        val htmlFile = FileUtils.createFile(inpFile.getPath, ".html")
        val pdfFile = FileUtils.createFile(inpFile.getPath, ".pdf")
        val proc = new ProcessBuilder(
          wkhtml2pdfExe, htmlFile.getPath, pdfFile.getPath)
        proc.inheritIO
        proc.start
      }
    }
  }
}

abstract class InitializableController {

  class RunProc(p: => Unit) extends Runnable {
    def run {
      p
    }
  }

  private var _pane: Pane = _
  private var _stage: Stage = _

  def init(stage: Stage, pane: Pane) {
    _stage = stage
    _pane = pane
    init
  }

  def init: Unit

  protected def stage: Stage = _stage
  protected def pane: Pane = _pane

  protected def lookup[T](id: String): T =
    pane.lookup("#" + id).asInstanceOf[T]
  protected def label(id: String): Label = lookup[Label](id)
  protected def textField(id: String): TextField = lookup[TextField](id)
  protected def choiceBox[T](id: String): ChoiceBox[T] =
    lookup[ChoiceBox[T]](id)
  protected def webView(id: String): WebView = lookup[WebView](id)
  protected def tabPane(id: String): TabPane = lookup[TabPane](id)
  protected def tab(id: String): Tab = lookup[Tab](id)

  protected def runLater(p: => Unit) {
    Platform.runLater(new RunProc(p))
  }
}
