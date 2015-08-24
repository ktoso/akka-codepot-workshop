package akka.codepot.engine.search.tiered.bottom

import java.io.File

import akka.stream.ActorMaterializer
import akka.stream.io.Framing
import akka.stream.scaladsl.Source
import akka.util.ByteString

import scala.concurrent.Future

trait IndexFileScanning {
  def materializer: ActorMaterializer
  def index: File

  private val ChunkSize = 4 * 2048

  import akka.stream.io.Implicits._
  val linesSource = Source.synchronousFile(index, chunkSize = ChunkSize)
    .via(Framing.delimiter(ByteString("\n"), Int.MaxValue))

  def scanFor(key: String, max: Int): Future[Vector[Term]] = {
    ??? // TODO implement
//    linesSource
//      .filter(parse(_).words.contains(key))
//      .take(max)
//      .runFold(Vector.empty[Term])()
  }

  // TODO refine these:
  case class Term(url: String, words: Set[String])
  private def parse(byteString: ByteString): Term = {
    // TODO parse the reverse index
    Term("", Set.empty)
  }
}
