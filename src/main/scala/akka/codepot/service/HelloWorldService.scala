package akka.codepot.service

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer

trait HelloWorldService extends Directives {
  implicit def system: ActorSystem
  implicit def materializer: ActorMaterializer

  def helloRoutes = // TODO make a hello world page!
    ??? // TODO maybe use ScalaXmlSupport?

  // TODO add a test?
}
