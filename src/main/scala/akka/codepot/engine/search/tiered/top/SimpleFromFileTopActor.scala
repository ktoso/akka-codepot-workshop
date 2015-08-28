package akka.codepot.engine.search.tiered.top

import akka.actor.{ActorLogging, Props, Stash, Actor}
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol
import akka.stream.scaladsl.{Sink, ImplicitMaterializer}
import akka.util.ByteString

import scala.collection.immutable

object  SimpleFromFileTopActor {
  def props(prefix: Char) =
    Props(classOf[SimpleFromFileTopActor], prefix)

  final case class PrepareIndex(char: Char)
  final case object IndexingCompleted
}
class SimpleFromFileTopActor(prefix: Char) extends Actor with ActorLogging
  with Stash
  with ImplicitMaterializer
  with Indexing {

  import ShardedSimpleFromFileTopActor._
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
      context.parent ! IndexingCompleted
      context become ready

    case _ => stash()
  }

  def ready: Receive = {
    case Search(keyword, maxResults) =>
      sender() ! Results(inMemIndex.find(_ contains keyword).take(maxResults).toList)
  }

  private def doIndex(char: Char): Unit =
    wikipediaCachedKeywordsSource
      .runWith(Sink.actorRef(self, onCompleteMessage = IndexingCompleted))

}
