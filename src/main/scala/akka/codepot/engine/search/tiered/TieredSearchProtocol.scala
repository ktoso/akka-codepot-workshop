package akka.codepot.engine.search.tiered

import scala.collection.immutable

object TieredSearchProtocol {
  final case class Search(keyword: String, maxResults: Int)

  final case class Results(strict: immutable.Seq[String])

}
