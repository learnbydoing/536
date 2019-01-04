import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorSystem}


object Server extends App {
  val system = ActorSystem("MapReduceSystem", ConfigFactory.load.getConfig("server1"))
  println("Server ready")
  //Thread.sleep(30000)
  //system.terminate
}
