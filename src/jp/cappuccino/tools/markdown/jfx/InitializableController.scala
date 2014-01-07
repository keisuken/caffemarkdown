package jp.cappuccino.tools.markdown.jfx

import javafx.application.Platform
import javafx.scene.layout.Pane
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.web.WebView
import javafx.stage.Stage


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
