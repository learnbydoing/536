package mapreduce

import akka.actor.{Actor, ActorRef}
import scala.io.Source
import scala.collection.mutable.HashMap


class MapActor(reduceActors: List[ActorRef]) extends Actor {

  val STOP_WORDS_LIST = List("a", "am", "an", "and", "are", "as", "at", "be",
    "do", "go", "if", "in", "is", "it", "of", "on", "the", "to")
  val numReducers = reduceActors.size
 

  def receive = {
   case SendInfo(t, u) =>
      process(t, u)
    
    case Flush => 
      for (i <- 0 until numReducers) {
        reduceActors(i) ! Flush
      }
    }

  def process(title: String, url: String) = {
    
    var pageContent = Source.fromURL(url).mkString 
    var words = HashMap[String, Int]()
    var pattern = "^[A-Z][a-z]+"
    var capWord = ""
    //println("content is" + pageContent) 
    for (word <- pageContent.split("[\\p{Punct}\\s]+")) 
    {
      if((!STOP_WORDS_LIST.contains(word))) 
      {
	//println("word is: " + word)
	if(word.matches(pattern) && !words.contains(word)) 
	{
	  capWord = word 
	  words += word -> 0
          //println("Cap word is: " + capWord)
          var index = Math.abs((word.hashCode())%numReducers)
         // println("Sending " + capWord + " to " + reduceActors(index))
          reduceActors(index) ! WordTitle(capWord, title)
       }
      }
     }
    }
}
