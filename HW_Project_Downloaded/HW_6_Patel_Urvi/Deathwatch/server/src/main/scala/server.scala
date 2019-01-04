import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorSystem}
import akka.actor.{Props, ActorSystem}


class RemoteActor extends Actor {
  def receive = {
    case msg: String =>
      println(s"RemoteActor received message '$msg'")
      sender ! "Hello from the RemoteActor"
  }
}
 
object RemoteActor extends App  {
  val system = ActorSystem("RemoteSystem", ConfigFactory.load.getConfig("server1"))
  val remoteActor = system.actorOf(Props[RemoteActor], name = "RemoteActor")
  remoteActor ! "The RemoteActor is alive"
}