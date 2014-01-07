package jp.cappuccino.tools.markdown.jfx

import javafx.event.Event
import javafx.event.EventHandler


object EventProc {
  def apply[E <: Event](proc: E => Unit) =
    new EventProc(proc)
}


class EventProc[E <: Event](proc: E => Unit) extends EventHandler[E] {
  def handle(event: E): Unit = proc(event)
}
