import akka.actor.{Actor, ActorRef}
import scala.io.Source
import scala.concurrent.duration._
import scala.collection.mutable.HashMap
import akka.routing.{Broadcast, RoundRobinPool}

import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}

class SubsequentException(msg: String) extends Exception

class MapActor(reduceActor: ActorRef) extends Actor {

  val STOP_WORDS_LIST = List("a", "am", "an", "and", "are", "as", "at", "be",
    "do", "go", "if", "in", "is", "it", "of", "on", "the", "to")
     
     
     
     println("Entered the MapActor constructor, this is: " + this)
     
     //override def preStart { 
         //println("kenny: preStart")
         //count = count + 1 
     //}
     
     override def preRestart(reason: Throwable, message: Option[Any]) {
        println("MESSAGE: " + message.getOrElse("") + "\this in preRestart is: "+ this)  
        super.preRestart(reason, message)
    }
    
    override def postRestart(reason: Throwable) {
  	super.postRestart(reason)
    println("this in postRestart is: " + this)
 }
  
    //override def postStop 
    //{ 
       //println("****** STOPPED in postStop ******")
       //println("Map actor: " + self.path.toString) 
       //println("sender: " + sender.path.toString) 
    //}
  
  def receive = {
   case SendInfo(t, u) =>
      process(t, u)
    
     case Flush => 
      reduceActor ! Broadcast(Flush)
    }

  def process(title: String, url: String) = {
    var pageContent = Source.fromURL(url).mkString 
    var words = HashMap[String, Int]()
    var pattern = "^[A-Z][a-z]+"
    var capWord = "" 
    for (word <- pageContent.split("[\\p{Punct}\\s]+")) 
    {
      if((!STOP_WORDS_LIST.contains(word))) 
      {
	     if(word.matches(pattern) && !words.contains(word)) 
	    {
	  		capWord = word 
	  		words += word -> 0
      		reduceActor ! SendInfo(capWord, title)
       }
      }
     }
    }
}
