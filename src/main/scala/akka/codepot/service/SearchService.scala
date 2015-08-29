package akka.codepot.service

import akka.actor.{ActorRef, ActorSystem}
import akka.codepot.engine.search.SearchMaster
import akka.codepot.engine.search.tiered.TieredSearchProtocol.{Results, Search}
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.xml.NodeSeq

trait SearchService extends Directives with ScalaXmlSupport {
  implicit def system: ActorSystem
  implicit def dispatcher = system.dispatcher
  implicit def materializer: ActorMaterializer

  // TODO explain ask pattern
  import akka.pattern.ask
  implicit val timeout = Timeout(3.seconds)

  // TODO what is this search master?
  lazy val searchMaster: ActorRef = system.actorOf(SearchMaster.props(), "searchMaster")

  def searchRoutes =
    // TODO "search" GET with params 'q and n, which is an Int, complete it with a search
      ???

  def search(q: String, max: Int): Future[NodeSeq] =
    ??? // TODO ask the SearchMaster to search for q, max 100 entries

  private def searchResultPage(results: Results): NodeSeq = {
    <ul>
      { results.strict.map { r => <li>{r}</li>} }
    </ul>
  }
}
