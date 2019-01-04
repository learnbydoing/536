import akka.actor.{Actor, ActorRef}
import scala.io.Source
import scala.collection.mutable.HashMap
import akka.routing.{Broadcast, RoundRobinPool}

import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}


class MapActor(reduceActor: ActorRef) extends Actor {

  val STOP_WORDS_LIST = List("a", "am", "an", "and", "are", "as", "at", "be",
    "do", "go", "if", "in", "is", "it", "of", "on", "the", "to")

  override def postRestart(reason: Throwable) {
  	super.postRestart(reason)
  	println("****************** RESTARTED ***************")
    println("Restart reason: " + reason.getMessage)
    println("Map actor: " + self.path.toString)
    println("Parent is: " + context.actorSelection("../"))
 }
  	

  
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
