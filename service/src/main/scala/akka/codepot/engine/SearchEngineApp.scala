package akka.codepot.engine

import akka.actor.ActorSystem
import akka.codepot.engine.search.SearchMaster

object SearchEngineApp extends App {
  implicit val system = ActorSystem("search-engine-system")

  val searchMaster = system.actorOf(SearchMaster.props())

}
