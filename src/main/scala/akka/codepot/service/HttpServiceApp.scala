package akka.codepot.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration._

object HttpServiceApp extends App
  with HelloWorldService
  with SearchService {

  // infrastructure:
  implicit val system = ActorSystem("search-system")
  implicit val materializer = ActorMaterializer()

  val config = system.settings.config

  // TODO handle exceptions?
  // ExceptionHandler for SearchEngineNotYetInitializedException

  // our routes:
  val route: Route =
    helloRoutes ~ searchRoutes

  // start the http server:
  val bindingFuture = Http().bindAndHandle(route, "127.0.0.1", config.getInt("codepot.http.port"))


  // cleanup:

  println("Server online at http://127.0.0.1:8080/")
  println("Press RETURN to stop...")
  io.StdIn.readLine()

  // cleanly unbind and shut down the ActorSystem
  bindingFuture
    .flatMap(_.unbind())(system.dispatcher) // trigger unbinding from the port
    .onComplete { _ ⇒ Await.ready(system.terminate(), 1.second) }

}