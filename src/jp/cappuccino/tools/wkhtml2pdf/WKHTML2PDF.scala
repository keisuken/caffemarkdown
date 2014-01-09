package jp.cappuccino.tools.wkhtml2pdf

import java.io.File
import java.io.IOException


import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader

class StreamSender(
  inp: InputStream, outp: (Array[Byte], Int) => Unit
) extends Thread {

  override def run {
    val buf = new Array[Byte](1024)
    try {
      while (!isInterrupted) {
        val len = inp.read(buf)
        if (len > 0) {
          outp(buf, len)
        } else {
          Thread.sleep(100)
        }
      }
    } catch {
      case exp: InterruptedException =>
    } finally {
      inp.close
    }
  }

  def close {
    interrupt
  }
}


class WKHTML2PDF(wkhtmltopdfFile: String) {

  @throws(classOf[IOException])
  def generate(
    htmlFile: File, pdfFile: File, outp: (Array[Byte], Int) => Unit
  ): Unit =
    generate(htmlFile.getPath, pdfFile.getPath, outp)

  @throws(classOf[IOException])
  def generate(
    htmlFile: String, pdfFile: String, outp: (Array[Byte], Int) => Unit
  ): Unit = {
    val procBuilder = new ProcessBuilder(wkhtmltopdfFile, htmlFile, pdfFile)
    val proc = procBuilder.start
    val ssOut = new StreamSender(proc.getInputStream, outp)
    ssOut.start
    val ssErr = new StreamSender(proc.getErrorStream, outp)
    ssErr.start
    try {
      proc.waitFor
    } finally {
      ssOut.close
      ssErr.close
    }
  }
}
