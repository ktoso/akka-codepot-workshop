package akka.codepot.engine.search.tiered.middle

import akka.actor.Actor
import akka.codepot.engine.search.tiered.TieredSearchProtocol

class MiddleTierActor extends Actor {
  import TieredSearchProtocol._

  def receive: Receive = {
    case ScanHit(key) =>
      ???
  }

}
