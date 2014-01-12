package jp.cappuccino.tools.markdown

import java.io.File

import util.FileUtils
import jp.cappuccino.tools.wkhtml2pdf.WKHTML2PDF


object Main extends App {

  final val Version = "0.2.0"
  final val Copyright = "NISHIMOTO Keisuke"

  final val WkHtml2Pdf = "wkhtmltopdf"

  final val OptionHelp = "-help"
  final val OptionVersion = "-version"
  final val OptionStyle = "-style"
  final val OptionWkhtmltopdf = "-wkhtmltopdf"
  final val OptionPdf = "-pdf"

  final lazy val stylesHome =
    System.getProperty("styles") match {
      case null => new File(".")
      case home => new File(home)
    }

  def prefs = java.util.prefs.Preferences.userNodeForPackage(getClass)


  def printErrLn(msg: String): Unit =
    System.err.println(msg)

  def printUsage: Unit = {
    printErrLn("Usage: caffemd [options] file...")
    printErrLn("Options:")
    printErrLn("  -help               Display this information")
    printErrLn("  -version            Display version information")
    printErrLn("  -style <style_name> Set output style")
    printErrLn("  -wkhtmltopdf <wkhtmltopdf_path>")
    printErrLn("                      Set wkhtmltopdf execution path")
    printErrLn("  -pdf                Output PDF file")
  }

  def printVersion: Unit = {
    printErrLn(s"Caffe Markdown ${Version}")
    printErrLn(s"Copyright (C) ${Copyright}.")
  }

  def containsHelp = args.contains(OptionHelp)
  def containsVersion = args.contains(OptionVersion)
  def getWkhtmltopdf: String = {
    args.indexOf(OptionWkhtmltopdf) match {
      case i if i >= 0 && (i + 1) < args.length =>
        val wkhtmltopdfPath = args(i + 1)
        prefs.put(WkHtml2Pdf, wkhtmltopdfPath)
        wkhtmltopdfPath
      case _ =>
        prefs.get(WkHtml2Pdf, "")
    }
  }
  def getStyleName: String =
    args.indexOf(OptionStyle) match {
      case i if i >= 0 && (i + 1) < args.length => args(i + 1)
      case _ => Style.Default
    }

  def containsPdf = args.contains(OptionPdf)
  def indexOfFile: Int =
    args.lastIndexWhere {_.startsWith("-")} match {
      case i if i < 0 => 0
      case i =>
        val option = args(i)
        if (option == OptionStyle || option == OptionWkhtmltopdf) {
          i + 2
        } else {
          i + 1
        }
    }
  def generateHtml(home: File, styleName: String, mdFile: File): Unit =
    Markdown.generate(home, styleName, mdFile)
  def generatePdf(wlhtml2pdfExe: String, mdFile: File): Unit = {
    val htmlFile = FileUtils.createFile(mdFile.getPath, ".html")
    val pdfFile = FileUtils.createFile(htmlFile.getPath, ".pdf")
    val wkhtml2pdf = new WKHTML2PDF(wlhtml2pdfExe)
    wkhtml2pdf.generate(
      htmlFile,
      pdfFile,
      {(b: Array[Byte], l: Int) => System.err.write(b, 0, l)}
    )
  }

  if (args.length == 0 || containsHelp) {
    printUsage
  } else if (containsVersion) {
    printVersion
  } else {
    val fileIndex = indexOfFile
    val home = new File(".")
    val styleName = getStyleName
    val wkhtmltopdfPath = getWkhtmltopdf
    val outputPdf = containsPdf && wkhtmltopdfPath != ""
    (fileIndex until args.length).foreach {index =>
      try {
        val file = new File(args(index))
        generateHtml(home, styleName, file)
        if (outputPdf) {
          generatePdf(wkhtmltopdfPath, file)
        }
      } catch {
        case t: Throwable =>
          printErrLn(s"Error: " + t.getMessage)
      }
    }
  }
}
