

import akka.actor.{Actor, ActorRef}
import scala.io.Source
import scala.collection.mutable.HashMap


class MapActor(reduceActors: List[ActorRef]) extends Actor {

  val STOP_WORDS_LIST = List("a", "am", "an", "and", "are", "as", "at", "be",
    "do", "go", "if", "in", "is", "it", "of", "on", "the", "to")
  val numReducers = reduceActors.size
 
  var pageContent = "" 

  def receive = {
   case SendInfo(t, u) =>
      process(t, u)
    
    case Flush => 
      println("In Flush in mapactor.scala numReducers is: " + numReducers)
      for (i <- 0 until numReducers) {
         println("reduceActors in mapactor.scala reduceActor(i) is: " + reduceActors(i))
        reduceActors(i) ! Flush
      }
    }

  def process(title: String, url: String) = {
    
    //For debugging purposes
     //pageContent = "Pickwick Papers was published in England.  Dickens, Dickens" 
      //if(title contains "Pickwick")
      //{
        //pageContent = "Pickwick Papers was published in England.  Dickens, Dickens" 
      //}
      //else if(title contains "Chuzzlewit")
      //{
		//	pageContent = "Chuzzlewit was published in England"
      //}
      //else if(title contains "Hunted")
      //{
		//	pageContent = "Not sure what Hunted was about"
      //}
      //else if(title contains "Expectations")
      //{
	//	pageContent = "It was the best of times, Dickens"
      //}
     
    var pageContent = Source.fromURL(url).mkString 
    var words = HashMap[String, Int]()
   //println("pageContent is: " + pageContent)
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
          //println("Sending " + capWord + " to " + reduceActors(index))
          reduceActors(index) ! SendInfo(capWord, title)
       }
      }
     }
    }
}
