package akka.codepot.engine.search

import akka.actor.Actor.Receive
import akka.actor.{Actor, Props}

object SearchMaster {
  def props(): Props =
    Props(classOf[SearchMaster])
}

class SearchMaster extends Actor {

  def indexing = ??? // TODO imeplement me
  override def receive: Receive = ???
}
