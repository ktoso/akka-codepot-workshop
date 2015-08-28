package akka.codepot.engine.search.tiered.bottom

import akka.actor.{Actor, Props}
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol
import akka.stream.scaladsl.ImplicitMaterializer

object BottomTierActor {
  def props(prefix: Char) =
    Props(classOf[BottomTierActor])

}

class BottomTierActor extends Actor with Indexing with ImplicitMaterializer {
  import TieredSearchProtocol._

  override def receive: Receive = {
    case ScanFor(key, max) => // TODO extract protocol

  }
}
