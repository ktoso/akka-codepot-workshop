package akka.codepot.engine.search.tiered.top

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol
import akka.stream.scaladsl.ImplicitMaterializer
import akka.util.ByteString

import scala.collection.immutable

object SimpleIndexedTopActor {
  def props(prefix: Char) =
    Props(classOf[SimpleIndexedTopActor], prefix)
}

class SimpleIndexedTopActor(prefix: Char) extends Actor with ActorLogging // TODO why ActorLogging and not directly sth?
  with Stash // TODO explain stash
  with ImplicitMaterializer // TODO explain why so
  with Indexing {

  import TieredSearchProtocol._

  var inMemIndex: immutable.Set[String] = Set.empty

  override def preStart() = // TODO explain preStart
    doIndex(prefix)

  // TODO this actor has 2 states
  override def receive: Receive = indexing

  def indexing: Receive = {
    case word: ByteString =>
      inMemIndex += word.utf8String
      logProgress()

    case IndexingCompleted =>
      val size = inMemIndex.size
      log.info("Indexing of {} keywords for prefix {} completed!", size, prefix)

      // TODO we're done indexing... we can `unstashAll`
      unstashAll()

      // TODO tell your parent IndexingCompleted
      // TODO become the `ready` receive function

    case _ => stash()
  }

  def ready: Receive = {
    case Search(keyword, maxResults) =>
      val result = searchInMem(keyword, maxResults)
      // log.info("Search for {}, resulted in {} entries, on shard {}", keyword, result.size, prefix)
      sender() ! SearchResults(result.toList)
  }

  def searchInMem(keyword: String, maxResults: Int): Set[String] =
    inMemIndex.filter(_ contains keyword)
      .take(maxResults)

  private def logProgress(): Unit = {
    val size = inMemIndex.size
    if (size % 1000 == 0) {
      log.info("Indexed {} keywords for {} prefix", size, prefix)
    }
  }

  private def doIndex(prefix: Char): Unit = {
    log.info("Indexing for index {}...", prefix)

    // TODO let's talk about Akka Streams and integrating them with Actors...
    // given the:
    wikipediaCachedKeywordsSource
      // TODO we want only the words that start with `prefix`
      // TODO we need to put them into our inMemIndex
    ???
  }

}
