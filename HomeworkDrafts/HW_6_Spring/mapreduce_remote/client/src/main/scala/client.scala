import akka.actor.{Actor, ActorRef, Props}
import akka.routing.{Broadcast, RoundRobinPool}
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorSystem, Props, AddressFromURIString, ActorLogging}
import akka.remote.routing.RemoteRouterConfig
import akka.routing.RoundRobinPool



object Client extends App {

  val system = ActorSystem("MapReduceLocalSystem", ConfigFactory.load.getConfig("remotelookup"))
  println("Client Ready")
  val client = system.actorOf(Props[ClientActor], name="clientUrvi")
  client ! START
}