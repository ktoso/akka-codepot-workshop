package akka.codepot.engine.search.tiered.top

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol
import akka.stream.scaladsl.{ImplicitMaterializer, Sink}
import akka.util.ByteString

import scala.collection.immutable

object  ShardedSimpleFromFileTopActor {
  def props() =
    Props(classOf[ShardedSimpleFromFileTopActor])

  final case class PrepareIndex(char: Char)
}
class ShardedSimpleFromFileTopActor extends Actor with ActorLogging
  with Stash
  with ImplicitMaterializer
  with Indexing {

  val prefix = self.path.name.head

  import TieredSearchProtocol._

  var inMemIndex: immutable.Set[String] = Set.empty

  override def preStart() =
    doIndex(prefix)

  override def receive: Receive = indexing

  def indexing: Receive = {
    case word: ByteString =>
      inMemIndex += word.utf8String

    case IndexingCompleted =>
      unstashAll()
      context become ready

    case _ => stash()
  }

  def ready: Receive = {
    case Search(keyword, maxResults) =>
      sender() ! SearchResults(inMemIndex.find(_ contains keyword).take(maxResults).toList)
  }

  private def doIndex(char: Char): Unit =
    wikipediaCachedKeywordsSource
      .runWith(Sink.actorRef(self, onCompleteMessage = IndexingCompleted))

}
