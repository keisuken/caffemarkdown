package jp.cappuccino.tools.markdown.jfx

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

import jp.cappuccino.tools.markdown.util.ClassUtils


class PaneApplication(
  title: String,
  fxmlPath: String,
  controller: InitializableController
) extends Application {

  @throws(classOf[Exception])
  override def start(stage: Stage) {
    val pane = FXMLUtils.load[Pane](fxmlPath, controller)
    controller.init(stage, pane)
    val scene = new Scene(pane)
    stage.setTitle(title)
    stage.setScene(scene)
    stage.show
  }
}
