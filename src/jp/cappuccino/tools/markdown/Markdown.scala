package jp.cappuccino.tools.markdown

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.script._

import util.FileUtils._


/**
 * Markdown utility.
 */
object Markdown {

  /** Resource path of marked source file. */
  private final val MarkedSourcePath =
    "jp/cappuccino/tools/markdown/res/marked.js"

  /** ClassLoader for this library. */
  private val classLoader = getClass.getClassLoader match {
    case null => java.lang.ClassLoader.getSystemClassLoader
    case classLoader => classLoader
  }

  /** Source of marked. */
  private val MarkdownSource: String = {
    val url = classLoader.getResource(MarkedSourcePath)
    val source = io.Source.fromURL(url, "utf-8")
    try {
      source.getLines.mkString("\n")
    } catch {
      case exp: IOException =>
        null
    } finally {
      source.close
    }
  }

  /** Script engine manager. */
  private val engineManager = new ScriptEngineManager(classLoader)

  /**
   * Generate.
   * @param  home  style home directory.
   * @param  styleName  style name.
   * @param  inpFile  source file name.
   * @return  generated HTML.
   */
  def generate(home: File, styleName: String, inpFile: File) {
    val markdown = new Markdown
    val source = load(inpFile)
    val style = Style.load(home, styleName)
    val title = titleFromMarkdown(inpFile.getName, source)
    val outpFile = new File(inpFile.getParent, htmlFileName(inpFile.getName))
    val html = markdown.generate(source, style, title)
    save(html, outpFile)
  }

  /**
   * Generate.
   * @param  home  style home directory.
   * @param  styleName  style name.
   * @param  inpFile  source file name.
   * @return  generated HTML.
   */
  def generate(home: String, styleName: String, inpFile: String): Unit =
    generate(new File(home), styleName, new File(inpFile))

  /**
   * Generate to raw elements.
   * @param  source  markdown source.
   * @param  options  marked option object.
   * @return  generated raw DOM.
   */
  def generateRaw(
    source: String, options: String = "{}"
  ): String = (new Markdown).generateRaw(source, options)

  /**
   * Create marked script engine.
   * @return  script engine.
   */
  protected def createMarked: ScriptEngine = {
    val engine = engineManager.getEngineByExtension("js")
    val source = MarkdownSource + "\nmarked"
    val marked = engine.eval(source)
    engine.put("marked", marked)
    engine
  }

  private def htmlFileName(fileName: String) =
    prefixFileName(fileName) + ".html"

  private def titleFromMarkdown(fileName: String, source: String): String = {
    """(?m)#*([^\r\n]*)""".r.findFirstMatchIn(source) match {
      case Some(mat) => mat.group(1)
      case None => prefixFileName(fileName)
    }
  }
}


/**
 * Markdown generator.
 */
class Markdown {

  private val engine = Markdown.createMarked

  /**
   * Generate.
   * @param  source  markdown source.
   * @param  style  style.
   * @param  title  HTML title.
   * @param  bodyOnly  if true then body only else width HTML header.
   * @return  generated HTML.
   */
  def generate(
    source: String, style: Style,
    title: String = "", bodyOnly: Boolean = false
  ): String = {
    engine.eval(style.initScript)
    engine.put("source", source)
    val body = engine.eval("marked(source)").toString
    if (bodyOnly)
      body
    else
"""<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>""" + xml.Text(title) + """</title>
    <style type="text/css">
""" + style.css + """</style>
  </head>
  <body>
""" +
body +
"""  </body>
</html>
"""
  }

  /**
   * Generate to raw elements.
   * @param  source  markdown source.
   * @param  options  marked option object.
   * @return  generated raw DOM.
   */
  def generateRaw(
    source: String, options: String = "{}"
  ): String = {
    engine.eval("marked.setOptions(" + options + ");")
    engine.put("source", source)
    engine.eval("marked(source)").toString
  }
}
