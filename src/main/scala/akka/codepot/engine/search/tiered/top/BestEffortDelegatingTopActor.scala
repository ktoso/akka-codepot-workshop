package akka.codepot.engine.search.tiered.top

import akka.actor._
import akka.codepot.engine.search.tiered.middle.RandomlySlowMiddleActor

import scala.concurrent.duration._


object BestEffortDelegatingTopActor {
  def props(prefix: Char, sla: FiniteDuration): Props =
    Props(classOf[BestEffortDelegatingTopActor], prefix, sla)
}

class BestEffortDelegatingTopActor(prefix: Char, sla: FiniteDuration) extends Actor {

  val worker = context.actorOf(RandomlySlowMiddleActor
    .props(context.parent, prefix, slowness = 200.millis, chance = 5 /* % */),
    name = "slowWorker")

  override def receive: Receive = {
    case any =>
      val replyTo = sender()

      // TODO implement per-request actor which "either finishes with success, or handles the timeout"
      // TODO use it to reply here.
      // TODO you can use setReceiveTimeout
      // TODO   or the scheduler...

      val slaGuardian: ActorRef = ???
      worker.tell(any, sender = slaGuardian)
  }
}
