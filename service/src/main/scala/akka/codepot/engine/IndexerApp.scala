package akka.codepot.engine

import java.io.File

import akka.actor.ActorSystem
import akka.codepot.common.FuzzyJsonBracketCountingFraming
import akka.stream.{Supervision, ActorAttributes, ActorMaterializer}
import akka.stream.io.Framing
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import spray.json._
import DefaultJsonProtocol._

object IndexerApp extends App {
  implicit val system = ActorSystem("indexing-app", ConfigFactory.parseResources("nothing.conf"))
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  import akka.stream.io.Implicits._

  private val ChunkSize = 8192

  val input = if (args.length == 1) args.head else "20150817.json"
  println(s"Indexing [$input]...")

  Source.synchronousFile(new File(input), ChunkSize)
    .via(FuzzyJsonBracketCountingFraming(allowTruncation = false))
    .map(_.utf8String.parseJson.asJsObject)
    .filter(_.fields("type").toString() == """"item"""")
    .map(_.fields("labels").asJsObject.fields("en").asJsObject.fields("value").toString().replaceAll("\"", "") + "\n")
    .map(in => {
//      print(in)
      ByteString(in)
    })
    .withAttributes(ActorAttributes.supervisionStrategy(_ => Supervision.Resume))
    .runWith(Sink.synchronousFile(new File("keywords.csv")))
    .onComplete(_ => system.terminate())


  io.StdIn.readLine("Press ENTER to quit...")
  system.terminate()
}
