package akka.codepot.engine.search.tiered.bottom

import java.io.File

import akka.actor.Actor
import akka.codepot.engine.search.tiered.TieredSearchProtocol
import akka.codepot.engine.search.tiered.TieredSearchProtocol.ScanFor
import akka.codepot.engine.search.tiered.middle.MiddleTierActor
import akka.stream.ActorMaterializer

object BottomTierActor {
  //
}

class BottomTierActor extends Actor with IndexFileScanning {
  import TieredSearchProtocol._

  override def materializer: ActorMaterializer = ActorMaterializer()
  override def index: File = ??? // TODO give it the index

  override def receive: Receive = {
    case ScanFor(key, max) => // TODO extract protocol

  }
}
