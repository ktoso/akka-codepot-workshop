package akka.codepot.engine.search.tiered.middle

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol
import akka.codepot.engine.search.tiered.top.ShardedSimpleFromFileTopActor
import akka.stream.scaladsl.{ImplicitMaterializer, Sink}
import akka.util.ByteString

import scala.collection.immutable
import scala.concurrent.duration.FiniteDuration

object  RandomlySlowMiddleActor {
  /**
   * @param chance percent (0 - 100)
   */
  def props(prefix: Char, slowness: FiniteDuration, chance: Int = 50) = {
    require(chance >= 0 && chance <= 100, "chance must be: >= 0 && <= 100")
    Props(classOf[RandomlySlowMiddleActor], prefix)
  }
}

class RandomlySlowMiddleActor(prefix: Char) extends Actor with ActorLogging
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
      sender() ! SearchResults(inMemIndex.find(_ contains keyword).take(maxResults).toList)
  }

  private def doIndex(char: Char): Unit =
    wikipediaCachedKeywordsSource
      .runWith(Sink.actorRef(self, onCompleteMessage = IndexingCompleted))

}
