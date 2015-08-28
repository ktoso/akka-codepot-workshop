package akka.codepot.engine.search

import akka.actor.{Actor, ActorLogging, Props}
import akka.codepot.engine.SearchEngineNotYetInitializedException
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol._
import akka.codepot.engine.search.tiered.top.SimpleIndexedTopActor
import akka.stream.scaladsl.ImplicitMaterializer
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._

object SearchMaster {
  def props(): Props =
    Props(classOf[SearchMaster])
}

class SearchMaster extends Actor with ImplicitMaterializer with ActorLogging
  with Indexing {

  import akka.pattern.{ask, pipe}
  import context.dispatcher
  implicit val timeout = Timeout(200.millis)

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

      // FUTURES, waits for all...
      val allResults =
        context.children
          .map(c => (c ? search).mapTo[Results])
          .toList

      val combined =
        Future.sequence(allResults.map(_.recover { case _ => SearchResults(Nil) }))
        .map(seq => SearchResults(seq.flatMap(_.strict)))

      combined.pipeTo(sender())

//    STREAMS, waits for `max`
//      val combined =
//        Source(context.children)
//        .mapAsyncUnordered(context.children.size) { worker =>
//          val start = System.currentTimeMillis()
//          (worker ? search)
//            .mapTo[Results]
//            .recover { case _ => SearchResults(Nil) }
//        }
//      .mapConcat(i => i.strict)
//      .runFold(List.empty[String])((acc, el) => el :: acc)
//
//      combined.map(SearchResults(_)).pipeTo(sender())
  }

}
