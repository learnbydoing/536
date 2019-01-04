package second;

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.ActorRef
import akka.actor.ActorSelection
import akka.actor.ActorPath

case class Start(token:String, fPath:String,  sPath:String, tPath:String)
case class Send(token:String, fPath:String, sPath:String, tPath:String)

class H2()  extends Actor {

 var tCount = 0
 var uCount = 0

  def receive = {
    case Send(token, firstPath, secondPath, thirdPath)  => 
     //println("Token is: " + token + " and Inside Send, firstPath, is: " + firstPath)
     //println("Token is: " + token + " and Inside Send, secondPath, is: " + secondPath)
     //println("Token is: " + token + " and Inside Send, thirdPath, is: " + thirdPath)
      if(token == "T")
      {
      	if(sender.path.toString contains "first")
      	{
           Thread.sleep(2000)
	   tCount = tCount + 1
	   println("1st actor, tCount is: " + tCount)
	  // println("For T, in first, secondPath is: " + secondPath)
	   val nextNode = context.actorSelection(secondPath)
	   //println("In first, nextNode is: " + nextNode)
	   nextNode ! Send(token, firstPath, secondPath, thirdPath) 
        }
       
      	else if(sender.path.toString contains "second")
      	{
           Thread.sleep(2000)
	   //tCount = tCount + 1
	   println("2nd actor, tCount is: " + tCount)
	   //println("For T, in second, thirdPath is: " + thirdPath)
	   val nextNode = context.actorSelection(thirdPath)
	   //println("In second, nextNode is: " + nextNode)
	   nextNode ! Send(token, firstPath, secondPath, thirdPath) 
   	}   

      	else if(sender.path.toString contains "third")
      	{
           Thread.sleep(2000)
	   tCount = tCount + 1
	   println("3rd actor,  tCount is: " + tCount)
	   //println("For T, in third, firstPath is: " + firstPath)
	    //val nextNode = context.actorSelection(firstPath)
	   //println("In third, nextNode is: " + nextNode)
	   nextNode ! Send(token, firstPath, secondPath, thirdPath) 
   	}   

     }
     if(token == "U")
     {
      	if(sender.path.toString contains "first")
      	{
           Thread.sleep(2000)
	   uCount = uCount + 1
	   println("1st actor, uCount is: " + uCount)
	   //println("For U, in first, thirdPath is: " + thirdPath)
	   val nextNode = context.actorSelection(thirdPath)
	   //println("For U, in first, nextNode is: " + nextNode)
	   nextNode ! Send(token, firstPath, secondPath, thirdPath) 
        }
       
      	if(sender.path.toString contains "second")
      	{
           Thread.sleep(2500)
	   uCount = uCount + 1
	   println("2nd actor, uCount is: " + uCount)
	   //println("For U, in second, firstPath is: " + firstPath)
	   val nextNode = context.actorSelection(firstPath)
	   //println("For U, in second, nextNode is: " + nextNode)
	   nextNode ! Send(token, firstPath, secondPath, thirdPath) 
   	}   

      	if(sender.path.toString contains "third")
      	{
           Thread.sleep(2500)
	   uCount = uCount + 1
	   //println("3rd actor, uCount is: " + uCount)
	   val nextNode = context.actorSelection(secondPath)
	   //println("For U, in third, nextNode is: " + nextNode)
	  nextNode ! Send(token, firstPath, secondPath, thirdPath) 
   	}   
     } //End if token == U 

    case Start(token, fPath, sPath, tPath)  =>
      var next = context.actorSelection(sPath)
      if(token == "T")
      {
	next = context.actorSelection(sPath)
        next ! Send("T", fPath, sPath, tPath)
      }
      else
      {
	next = context.actorSelection(tPath)
        next ! Send("U", fPath, sPath, tPath)
      }
  }
}

object hw2Server extends App {
  val system = ActorSystem("H2")
  val first = system.actorOf(Props[H2], name = "first")
  val second = system.actorOf(Props[H2], name = "second")
  val third = system.actorOf(Props[H2], name = "third")
  //Paths.p1 = first.path.toString
  //Paths.p2 = second.path.toString
  //Paths.p3 = third.path.toString
  println(first.path)
  first ! Start("T", first.path.toString, second.path.toString, third.path.toString)
  first ! Start("U", first.path.toString, second.path.toString, third.path.toString)
  println("Server IS  ready")
}
