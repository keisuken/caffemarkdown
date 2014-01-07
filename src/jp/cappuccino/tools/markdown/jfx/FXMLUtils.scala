package jp.cappuccino.tools.markdown.jfx

import javafx.fxml.FXMLLoader

import jp.cappuccino.tools.markdown.util.ClassUtils


object FXMLUtils {

  def load[T](path: String, controller: Object): T = {
    val fxmlLoader = new FXMLLoader(ClassUtils.resource(path))
    if (controller != null) {
      fxmlLoader.setController(controller)
    }
    fxmlLoader.load.asInstanceOf[T]
  }
}
