package jp.cappuccino.tools.markdown

import java.io.{File, FileOutputStream, OutputStreamWriter}


object FileUtils {

  def load(file: String): String = {
    val source = io.Source.fromFile(file, "utf-8")
    try {
      source.getLines.mkString("\n")
    } finally {
      source.close
    }
  }

  def load(file: File): String =
    load(file.getPath)

  def save(text: String, file: String) {
    val outp =
      new OutputStreamWriter(
        new FileOutputStream(file), "utf-8")
    try {
      outp.write(text)
    } finally {
      outp.close
    }
  }

  def save(text: String, file: File): Unit =
    save(text, file.getPath)


  def prefixFileName(fileName: String) =
    fileName.replaceAll("\\.[^\\.]+$", "")


  def createFile(fileName: String, ext: String): File =
    new File(prefixFileName(fileName) + ext)

}
