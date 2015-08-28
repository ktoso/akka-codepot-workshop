package akka.codepot.engine.search.tiered.top

import akka.actor.{Props, Actor}
import akka.codepot.engine.search.tiered.middle.RandomlySlowMiddleActor

object NoopDelegatingTopActor {
  def props(prefix: Char) = Props(classOf[TailChoppingDelegatingTopActor], prefix: Char)
}

class NoopDelegatingTopActor(prefix: Char) extends Actor {

  val worker = context.actorOf(RandomlySlowMiddleActor.props(prefix))

  override def receive: Receive = {
    case any => worker forward any
  }
}
