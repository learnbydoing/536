import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorSystem, Props, AddressFromURIString, ActorLogging, Terminated}
import akka.util.Timeout
import scala.concurrent.duration._

case object START

class LocalActor extends Actor {
    
    val path = "akka.tcp://RemoteSystem@127.0.0.1:2557/user/RemoteActor"
    val remoteActor = scala.concurrent.Await.result(context.actorSelection(path).resolveOne()(Timeout(5 seconds)), 10.seconds)
  
  def receive = {
    case Terminated(corpse) => {
       if(corpse == remoteActor) {
           println("Watched actor is dead\t" + corpse.path.toString)
           context.system.terminate
         }
      }
      
       case msg: String => {
          println("I am watching")
       } 
        
     case START => {
        context.watch(remoteActor)
       }
    }
  }
  
  object LocalActor extends App {

  val system = ActorSystem("LocalSystem", ConfigFactory.load.getConfig("remotelookup"))
  println("Client Ready")
  val localClient = system.actorOf(Props[LocalActor], name="localClient")
  localClient ! START
 }
