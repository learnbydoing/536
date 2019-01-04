package second;

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.ActorRef
import akka.actor.ActorSelection
import akka.actor.ActorPath

case class Start(token:String)
case class Send(token:String)

object First {
  var tCount = 0
  var uCount = 0
  private def incT = {tCount += 1; tCount}
  private def incU = {uCount += 1; uCount}
}

class First extends Actor {
   
    var tCountFirst=0
    var uCountFirst=0
    var next = context.actorOf(Props[First], name="next")

   def receive = {
      case Send(token) =>
	Thread.sleep(500)
        if(token == "T") 
	{
	   next = context.actorOf(Props[Second], name="second")
	   tCountFirst = First.incT
	   println("CW token count for Actor 1 is: " + tCountFirst)
        }
        else
        {
	   next = context.actorOf(Props[Third], name="third")
	   uCountFirst = First.incU
	   println("CCW token count for Actor 1 is: " + uCountFirst)
        }	
        next ! Send(token)         
      case Start(token) =>
	Thread.sleep(500)
        if(token == "T")
        {
	   next = context.actorOf(Props[Second], name="second")
	   tCountFirst = First.incT
	   println("CW token count for Actor 1 is: " + tCountFirst)
        }
        else
        {
            next = context.actorOf(Props[Third], name="third")
	    uCountFirst = First.incU
            println("CCW token count for Actor 1 is: " + uCountFirst) 
        } 
        next ! Send(token)         
}// End receive()
}//End class First

object Second {
  var tCount = 0
  var uCount = 0
  private def incT = {tCount += 1; tCount}
  private def incU = {uCount += 1; uCount}
}

class Second extends Actor {

    var tCountSecond = 0
    var uCountSecond = 0
    var next = context.actorOf(Props[Second], name="next")

   def receive = {
       case Send(token) =>
	Thread.sleep(500)
        if(token == "T")
        {
           next = context.actorOf(Props[Third], name="third")
	   tCountSecond = Second.incT
	   println("CW token count for Actor 2 is: " + tCountSecond)
        }
        else
        {
           next = context.actorOf(Props[First], name="first")
           uCountSecond = Second.incU
           println("CCW token count for Actor 2 is: " + uCountSecond)
        }
	next ! Send(token)
}// End receive()
}//End class Second

object Third {
  var tCount = 0
  var uCount = 0
  private def incT = {tCount += 1; tCount}
  private def incU = {uCount += 1; uCount}
}

class Third extends Actor {

    var tCountThird = 0
    var uCountThird = 0
    var next = context.actorOf(Props[Third], name="next")

   def receive = {
       case Send(token) =>
	Thread.sleep(500)
        if(token == "T")
	{
          next = context.actorOf(Props[First], name="first")
	  tCountThird = Third.incT
	  println("CW token count for Actor 3 is: " + tCountThird)
        }
        else
        {
           next = context.actorOf(Props[Second], name="second")
           uCountThird = Third.incU
           println("CCW token count for Actor 3 is: " + uCountThird)
        }
       next ! Send(token)
}// End receive()
}//End class Third

object MainApp extends App {
	val system = ActorSystem("H2Test")
	val first = system.actorOf(Props[First], name="first")
        first ! Start("T")
        first ! Start("U")
} //End MainApp
