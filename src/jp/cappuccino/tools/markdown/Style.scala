package jp.cappuccino.tools.markdown

import java.io.File

import FileUtils.{load => fLoad}

/**
 * Style utility.
 */
object Style {

  /** Default of style name. */
  final val Default = "default"

  /**
   * Load style.
   * @param  home  style home directory.
   * @param  name  style name.
   * @return  loaded style.
   */
  def load(home: String, name: String): Style = {
    val dir = new File(home + File.separator + "styles", name)
    val initScriptFile = new File(dir, "init.js")
    val cssFile = new File(dir, "style.css")
    new Style(fLoad(initScriptFile), fLoad(cssFile))
  }

  /**
   * Load style.
   * @param  home  style home directory.
   * @param  name  style name.
   * @return  loaded style.
   */
  def load(home: File, name: String): Style =
    load(home.getPath, name)

  /**
   * Find style names.
   * @param  home  style home directory.
   * @return  found style names.
   */
  def names(home: String): Seq[String] = {
    val dir = new File(home, "styles")
    dir.listFiles.filter {_.isDirectory}.map {_.getName}.toList
  }

  /**
   * Find style names.
   * @param  home  style home directory.
   * @return  found style names.
   */
  def names(home: File): Seq[String] =
    names(home.getPath)
}


/**
 * Style for markdown.
 */
case class Style(
  /** Initialize JavaScript. */
  initScript: String,
  /** Embbed CSS. */
  css: String
)
