package client

import scala.collection.mutable.HashMap
import akka.actor.{Actor, Props, Address}
import akka.routing.{Broadcast, RoundRobinPool, ConsistentHashingPool}
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import akka.remote.routing.RemoteRouterConfig
import com.typesafe.config.ConfigFactory
import common._

  class MasterActor[K,V,X,Y,U](input:HashMap[K,V], mapFn: (K,V) => List[(X,Y)], agg: List[(X,Y)] => HashMap[X, List[Y]], redFn: (X, List[Y]) => (X,U)) extends Actor {

  val numberMappers  = ConfigFactory.load.getInt("number-mappers")
  val numberReducers  = ConfigFactory.load.getInt("number-reducers")
  

  var pending = numberReducers

  val addresses = Seq(
    Address("akka", "MapReduceClient"),
    Address("akka.tcp", "MapReduceServer", "127.0.0.1", 2552)
  )

  def hashMapping: ConsistentHashMapping = {
    case REDUCEME => REDUCEME
    case ReduceIt(k,v) => k
  }
  
  val reduceActors = context.actorOf(RemoteRouterConfig(ConsistentHashingPool(numberReducers, hashMapping = hashMapping), addresses).props(Props(classOf[ReduceActor[X,Y,U]], redFn)))
  val mapActors = context.actorOf(RemoteRouterConfig(RoundRobinPool(numberMappers), addresses).props(Props(classOf[MapActor[K,V,X,Y]], reduceActors, input, mapFn, agg)))
  
  def receive = {
  
    case MAPME =>
        mapActors ! MAPME
        
        
    case Flush =>
      mapActors ! Broadcast(Flush)
      
    case Done =>
      println("Received Done from" + sender)
      pending -= 1
      if (pending == 0)
        context.system.terminate
  }
}
