package jp.cappuccino.tools.markdown.util


object ClassUtils {

  def resource(path: String): java.net.URL =
    getClass.getResource(path) match {
      case null => getClass.getClassLoader.getResource(path)
      case res => res
    }
}
