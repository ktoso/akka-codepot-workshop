package akka.codepot.engine.search

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ShardRegion.{ExtractEntityId, ExtractShardId}
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol._
import akka.stream.scaladsl.ImplicitMaterializer
import akka.util.Timeout

import scala.concurrent.duration._

object SearchMaster {
  def props(): Props =
    Props(classOf[SearchMaster])
}

class SearchMaster extends Actor with ImplicitMaterializer with ActorLogging
  with Indexing {
  implicit val timeout = Timeout(10.seconds)

  val extractShardId: ExtractShardId = ???
    // TODO shardId is more general... what could it be?

  val extractEntityId: ExtractEntityId = ???
    // TODO entityId can be more specific... what could it be?

  val workerShading: ActorRef = {
    ClusterSharding(context.system)
    ??? // TODO start cluster sharding
    // TODO shard the ShardedSimpleFromFileTopActor actor
  }

  override def receive: Receive = {
    case search: Search =>
      import akka.pattern.{ask, pipe}
      import context.dispatcher

      // TODO, see this is now a simple ask
      // TODO, ask's are equivalent to per-request actors "aggregators"
      (workerShading ? search).pipeTo(sender())
  }

}
