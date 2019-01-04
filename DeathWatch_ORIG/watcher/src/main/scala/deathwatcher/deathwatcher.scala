package deathwatcher

import common._
import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorRef, ActorLogging, ActorSystem, Props, AddressFromURIString}

object DeathWatcher extends App {
  val system = ActorSystem("DeathWatcher", ConfigFactory.load.getConfig("remotelookup"))
  println("Watcher is Ready.")
  val supervisor = system.actorOf(Props[Supervisor], name="supervisor")
  println(supervisor.path)
  
  def userinput: Boolean = {
    var shutdown = readLine("Finished? (Enter Yes)> ")
    if(shutdown.contains("yes") || shutdown.contains("Yes") || shutdown.contains("y")){
      supervisor ! KILL
      Thread.sleep(500)
      system.shutdown
      false
    }
    else{
      println("Please enter yes when finished.")
      true
    }
  }
  //prompt for shutdown
  while(userinput){
    userinput
  }
}
