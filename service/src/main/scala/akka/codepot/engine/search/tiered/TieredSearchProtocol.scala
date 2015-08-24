package akka.codepot.engine.search.tiered

import scala.collection.immutable

object TieredSearchProtocol {
  final case class Search(keyword: String, maxResults: Int)

  trait UpstreamProtocol
  case class Results(strict: immutable.Seq[String]) extends UpstreamProtocol // TODO improve

  trait DownstreamProtocol
  final case class ScanFor(keyword: String, maxResults: Int) extends DownstreamProtocol
  final case class ScanHit(keyword: String) extends DownstreamProtocol

}
