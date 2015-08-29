package akka.codepot.service

import akka.actor.{ActorRef, ActorSystem}
import akka.codepot.engine.search.SearchMaster
import akka.codepot.engine.search.tiered.TieredSearchProtocol.{Search, SearchResults}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future
import scala.concurrent.duration._

trait SearchService extends Directives with ScalaXmlSupport
  with SprayJsonSupport {
  implicit def system: ActorSystem
  implicit def dispatcher = system.dispatcher
  implicit def materializer: ActorMaterializer

  import akka.pattern.ask
  implicit val timeout = Timeout(10.seconds)

  lazy val searchMaster: ActorRef = system.actorOf(SearchMaster.props(), "searchMaster")

  import DefaultJsonProtocol._
  implicit val results = jsonFormat1(SearchResults)

  def searchRoutes =
    pathPrefix("search") {
      get {
        parameters('q, 'n ? 100) { (q, max) =>
          complete { search(q, max) }
        }
      } ~
      complete {
        <div>
          <h1>Say hello to akka-http</h1>
          <form action="/search">
            <input name="q"></input>
            <button value="Search!"/>
          </form>
        </div>
      }
    }

  def search(q: String, max: Int): Future[SearchResults] =
    (searchMaster ? Search(q, max)).mapTo[SearchResults]

}
