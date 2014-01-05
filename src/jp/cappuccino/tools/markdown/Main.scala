package jp.cappuccino.tools.markdown

import java.io.File


object Main extends App {

  final lazy val stylesHome =
    System.getProperty("styles") match {
      case null => new File(".")
      case home => new File(home)
    }

  args.length match {
    case 1 =>
      Markdown.generate(new File("."), Style.Default, new File(args(0)))
    case 2 =>
      Markdown.generate(new File("."), args(0), new File(args(1)))
    case _ =>
      println("Usage: caffemd [style name] input")
  }
}
