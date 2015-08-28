package akka.codepot.user

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, _}
import akka.stream.ActorMaterializer

import scala.concurrent.Future

class UserRunner extends App {
  // infrastructure:
  implicit val system = ActorSystem("user-system")
  implicit val materializer = ActorMaterializer()

  val simpleQueries =
    List.fill(10 * 1000)(List("apple", "intel", "red", "poland", ""))
      .flatten
      .permutations.next()

  simpleQueries foreach { q =>
    val eventualResponse: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(uri = s"http://127.0.0.1/search?q=$q"))
    eventualResponse
  }

}
