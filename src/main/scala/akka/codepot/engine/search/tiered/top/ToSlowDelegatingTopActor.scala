package akka.codepot.engine.search.tiered.top

import akka.actor.{Props, Actor}
import akka.codepot.engine.search.tiered.TieredSearchProtocol.IndexingCompleted
import akka.codepot.engine.search.tiered.middle.RandomlySlowMiddleActor
import scala.concurrent.duration._


object ToSlowDelegatingTopActor {
  def props(prefix: Char): Props =
    Props(classOf[ToSlowDelegatingTopActor], prefix: Char)
}

class ToSlowDelegatingTopActor(prefix: Char) extends Actor {

  val worker = context.actorOf(RandomlySlowMiddleActor
    .props(prefix, slowness = 100.millis, chance = 25 /* % */),
    name = "slowWorker")

  override def receive: Receive = {
    case IndexingCompleted => context.parent ! IndexingCompleted
    case any => worker forward any
  }
}
