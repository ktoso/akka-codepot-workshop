package akka.codepot.service

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._

class HelloWorldServiceSpec extends WordSpec with Matchers with ScalatestRouteTest
with HelloWorldService {

  val route = helloRoutes

  "Hello World service" must {

    "contain hello world" in {
      Get("/hello") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] should include ("hello")
      }
    }
  }

}