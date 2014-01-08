package jp.cappuccino.tools.wkhtml2pdf

import java.io.File
import java.io.IOException


class WKHTML2PDF(wkhtmltopdfFile: String) {

  @throws(classOf[IOException])
  def generate(htmlFile: File, pdfFile: File): Unit =
    generate(htmlFile.getPath, pdfFile.getPath)

  @throws(classOf[IOException])
  def generate(htmlFile: String, pdfFile: String) {
    val proc = new ProcessBuilder(wkhtmltopdfFile, htmlFile, pdfFile)
    proc.inheritIO
    proc.start
  }
}
