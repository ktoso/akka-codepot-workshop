package akka.codepot.engine.search

import akka.actor.{Actor, Props}
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol
import akka.codepot.engine.search.tiered.top.SimpleFromFileTopActor
import akka.stream.scaladsl.ImplicitMaterializer

object SearchMaster {
  def props(): Props =
    Props(classOf[SearchMaster])
}

class SearchMaster extends Actor with ImplicitMaterializer
  with Indexing {

  val shards = 'A' to 'Z'
  val workers = shards foreach { l => context.actorOf(SimpleFromFileTopActor.props(l), s"$l") }

  override def receive: Receive = initializingWorkers(shards.size)

  def initializingWorkers(leftToInitialize: Int): Receive =
    if (leftToInitialize == 0) {
      initialized
    } else {
      case SimpleFromFileTopActor.IndexingCompleted =>
        context become initializingWorkers(leftToInitialize - 1)
      case _ =>
        sender() ! "search-engine-not-yet-initialized" // TODO can be stash
    }

  def initialized: Receive = {
    case search @ TieredSearchProtocol.Search(key, max) =>
      for {
        shard <- shards
        k <- key.headOption
        if shard == k
        worker <- context.child(shard.toString)
      } worker ! search
  }

}
