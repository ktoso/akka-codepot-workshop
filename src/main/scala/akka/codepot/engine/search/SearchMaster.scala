package akka.codepot.engine.search

import akka.actor.{Actor, ActorLogging, Props}
import akka.codepot.engine.SearchEngineNotYetInitializedException
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol._
import akka.codepot.engine.search.tiered.top.SimpleIndexedTopActor
import akka.stream.scaladsl.ImplicitMaterializer
import akka.util.Timeout

import scala.concurrent.duration._

object SearchMaster {
  def props(): Props =
    Props(classOf[SearchMaster])
}

class SearchMaster extends Actor with ImplicitMaterializer with ActorLogging
  with Indexing {
  implicit val timeout = Timeout(200.millis)

  // TODO we're creating workers here, for each letter
  val shards = 'A' to 'Z'
  val workers = shards foreach { l =>
    context.actorOf(SimpleIndexedTopActor.props(l), s"$l")
  }

  override def receive: Receive = initializingWorkers(shards.size)

  def initializingWorkers(leftToInitialize: Int): Receive =
    if (leftToInitialize == 0) initialized
    else {
      case IndexingCompleted =>
        context become initializingWorkers(leftToInitialize - 1)
      case _ =>
        sender() ! SeachFailed(SearchEngineNotYetInitializedException(
          "Search engine not yet initialized! " +
          s"Waiting for $leftToInitialize more workers..."))
    }

  def initialized: Receive = {
    case search @ Search(key, max) =>

      // TODO ask all downstreams
      // TODO NOT fail when one of them failed
      // TODO pipeTo sender() after all data collected
      // FUTURES, waits for all...
      val allResults =
        context.children

      ???

      // TODO how long are you waiting?
      // TODO could you wait less?
      // TODO how?
  }

}
