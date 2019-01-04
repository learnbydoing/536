package server

import common._
import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, OneForOneStrategy}

class MasterActor extends Actor {

  // Test Supervisor by sending input
  def test(supervisor: ActorRef) = {
    supervisor ! 4
    supervisor ! "anything"
    supervisor ! 2
    supervisor ! "stateError"
    supervisor ! 3
    supervisor ! 0
    supervisor ! 5 // never reached
    println("Finished Testing Supervisor.")  
  }

  def receive = {
    case CONNECT => {
      println("Supervisor connected at " + sender.path + ".")
      test(sender)
    }
    case _ => {
      println("Unknown Message Received.")
    }
  }
}
  
object Server extends App {
  val system = ActorSystem("Server", ConfigFactory.load.getConfig("server"))
  val master = system.actorOf(Props[MasterActor], name="masteractor")
  
  def userinput: Boolean = {
    var shutdown = readLine("Shutdown? (Enter Yes)> ")
    if(shutdown.contains("yes") || shutdown.contains("Yes") || shutdown.contains("y")){
      system.shutdown
      false
    }
    else{
      println("Please enter yes to shutdown.")
      true
    }
  }
  //prompt for shutdown
  while(userinput){
    userinput
  }
}
