import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorRef, ActorLogging, ActorSystem, Props, AddressFromURIString}
import akka.remote.routing.RemoteRouterConfig
import akka.routing.RoundRobinPool
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import akka.routing.ConsistentHashingPool
import akka.routing.{Broadcast, RoundRobinPool}

import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}

import java.io.FileNotFoundException
import java.net.UnknownHostException


class MasterActor extends Actor {
  
  	val numberMappers  = ConfigFactory.load.getInt("number-mappers")
    val numberReducers  = ConfigFactory.load.getInt("number-reducers")
    var pending = numberReducers
    
    val addresses = Seq(AddressFromURIString("akka://MapReduceLocalSystem"),AddressFromURIString("akka.tcp://MapReduceSystem@127.0.0.1:2555"),AddressFromURIString("akka.tcp://MapReduceSystem@127.0.0.1:2554"))
    //val addresses = Seq(AddressFromURIString("akka://MapReduceLocalSystem"),AddressFromURIString("akka.tcp://MapReduceSystem@127.0.0.1:2555"))
    
    override val supervisorStrategy =  OneForOneStrategy() {
      	case e: FileNotFoundException =>
      	 println("Failed actor in FileNotFound: " + sender)
      	 Restart
      	 
      	 case f: UnknownHostException =>
      	 println("Failed actor in UnknownHost: " + sender)
      	 Restart
      }

  
  def hashMapping: ConsistentHashMapping = 
  {
  	 	case SendInfo(key, value) => key
  	 	//case Flush => Flush
  }
     
    val reduceActor = context.actorOf(RemoteRouterConfig(ConsistentHashingPool(6, hashMapping=hashMapping), addresses).props(Props[ReduceActor]), "reducer")
    //val mapActor = context.actorOf(RemoteRouterConfig(RoundRobinPool(6), addresses).props(Props(classOf[MapActor], reduceActor)))
    //val mapActor = context.actorOf(RemoteRouterConfig(RoundRobinPool(6), addresses).props(Props(classOf[MapActor], reduceActor)), "mapActor")
    val mapActor = context.actorOf(RemoteRouterConfig(RoundRobinPool(6, supervisorStrategy=supervisorStrategy), addresses).props(Props(classOf[MapActor], reduceActor)), "mapActor")

  def receive = {
    case SendInfo(t, u) =>
      mapActor ! SendInfo(t, u) 
    case Flush =>
      mapActor ! Broadcast(Flush)
    case Done =>
      pending -= 1
      if (pending == 0){
        println("************* TERMINATING **************")
        context.system.terminate
     }
   }
}