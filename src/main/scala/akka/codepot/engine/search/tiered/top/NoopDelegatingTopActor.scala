package akka.codepot.engine.search.tiered.top

import akka.actor.{Props, Actor}
import akka.codepot.engine.search.tiered.middle.RandomlySlowMiddleActor
import scala.concurrent.duration._


object NoopDelegatingTopActor {
  def props(prefix: Char): Props =
    Props(classOf[NoopDelegatingTopActor], prefix: Char)
}

class NoopDelegatingTopActor(prefix: Char) extends Actor {

  val worker = context.actorOf(RandomlySlowMiddleActor
    .props(prefix, slowness = 200.millis, chance = 50 /* % */))

  override def receive: Receive = {
    case any => worker forward any
  }
}
