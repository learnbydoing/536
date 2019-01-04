package common

import scala.collection.mutable.HashMap
import scala.io.Source
import akka.actor.{Actor, ActorRef}

class MapActor(reduceActors: List[ActorRef]) extends Actor {

  val STOP_WORDS_LIST = List("a", "am", "an", "and", "are", "as", "at", "be", "do", "go", "if", "in", "is", "it", "of", "on", "the", "to")
  val numReducers = reduceActors.size
  val myHashMap = HashMap[String, String]()

  def receive = {
    case MAP(title: String, url: String) =>
      println("In mapactor.scala's MAP case, title is: " + title)
      println("In mapactor.scala's MAP case, url is: " + url)
      process(title, url)
    case Flush =>
      for(i <- 0 until numReducers){
        reduceActors(i) ! Flush
      }
  }

  def process(title: String, url: String) = {
    var content = Source.fromURL(url).mkString
    println("In mapactor.scala's process(), title is: " + title)
    println("In mapactor.scala's process(), url is: " + url)
    println("content is: " + content)
    for(name <- content.split("[\\p{Punct}\\s]+")){
      if((!STOP_WORDS_LIST.contains(name.toLowerCase)) && (name.matches("([A-Z][a-z]*\\s*)+"))){
        myHashMap += (name -> url)
      }
    }
    myHashMap.keys.foreach{ name =>
      var index = Math.abs((name.hashCode())%numReducers)
      println("Sending " + name + " and " + title + " to " + reduceActors(index))
      reduceActors(index) ! REDUCE(name, title)
    }
  }
}
