package second;

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.ActorRef
import akka.actor.ActorSelection
import akka.actor.ActorPath

case class Start(token:String, sPath:String, tPath:String)
case class Send(token:String, fPath:String, sPath:String, tPath:String)

object Paths {
	var p1 = ""
	var p2 = ""
    	var p3 = ""
}

class H2()  extends Actor {

 var tCount = 0

  def receive = {
    case Send(token, firstPath, secondPath, thirdPath)  => 
      println("In Send, first path is: " + firstPath)
      println("In Send, second path is: " + secondPath)
      println("In Send, Third path is: " + thirdPath)

      println("Token from " + sender.path + " is: " + token)
      Thread.sleep(2000)
      if(token == "T")
      {
      	if(sender.path.toString contains "first")
      	{
           Thread.sleep(2000)
           //println("Inside first, sender.path is:" +  sender.path)
           //println("First sends to second")
	   //println("Inside first, from is: " + sender)

          println("First path is: " + Paths.p1)
          println("Second path is: " + Paths.p2)
          println("Third is path: " + Paths.p3)
           
          println("In first, firstPath is: " + firstPath)
          println("In first, secondPath is: " + secondPath)
          println("In first, thirdPath is: " + thirdPath)

	   val nextNode = context.actorSelection(secondPath)
	   println("In first, nextNode is: " + nextNode)
	   nextNode ! Send(token, firstPath, secondPath, thirdPath) 
       }
       
      	if(sender.path.toString contains "second")
      	{
           Thread.sleep(500)
           //println("Inside second, sender.path is:" +  sender.path)
           //println("Second sends to third")
	  // println("Inside second, from is: " + sender)

           
          println("First path is: " + Paths.p1)
          println("Second path is: " + Paths.p2)
          println("Third is path: " + Paths.p3)

          println("In second, firstPath is: " + firstPath)
          println("In second, secondPath is: " + secondPath)
          println("In second, thirdPath is: " + thirdPath)

	   val nextNode = context.actorSelection(thirdPath)
	   println("In second, nextNode is: " + nextNode)
	   nextNode ! Send(token, firstPath, secondPath, thirdPath) 
   	}   

      	if(sender.path.toString contains "third")
      	{
           Thread.sleep(500)
           //println("Inside first, sender.path is:" +  sender.path)
           println("Third sends to first")
	   //println("Inside third, from is: " + sender)
           
          println("First path is: " + Paths.p1)
          println("Second path is: " + Paths.p2)
          println("Third is path: " + Paths.p3)

          println("In third, firstPath is: " + firstPath)
          println("In third, secondPath is: " + secondPath)
          println("In third, thirdPath is: " + thirdPath)

	   val nextNode = context.actorSelection(firstPath)
	   println("In third, nextNode is: " + nextNode)
	  nextNode ! Send(token, firstPath, secondPath, thirdPath) 
   	}   

     }
     /*if(token == "U")
     {
      if(sender.path.toString contains "second")
      {
        Thread.sleep(500)
        println("Inside second, sender.path is:" +  sender.path)
	println("Second sends to third")
	//third ! "T"
      }
      if(sender.path.toString contains "third")
      {
        Thread.sleep(500)
        println("Inside second, sender.path is:" +  sender.path)
	println("Third sends to first")
	//first ! "T"
      }
     } */
    case Start(token, sPath, tPath)  =>
      //firstPath = fPath
      //secondPath = sPath
      //thirdPath = tPath
      //println("Inside Start, firstPath is: " + firstPath)
	println("sender is: " + sender)
      //val firstNode  = context.actorSelection(fPath)
      //val secondNode = context.actorSelection(sPath)
      //val thirdNode = context.actorSelection(tPath)
      //println("First is: " + firstNode.path())
      //println("Second is: " + secondNode)
      //println("Third is: " + thirdNode)
      //val third = system.actorOf(Props[H2], name = "third")
      //println(secondPath)
      println("p1 is: " + Paths.p1)
      var next = context.actorSelection(sPath)
      if(token == "T")
      {
	next = context.actorSelection(sPath)
      }
      else
      {
	next = context.actorSelection(tPath)
      }
      next ! Send("T", Paths.p1, sPath, tPath)
      //next ! Start("T", sPath, tPath)
  }
}

object hw2Server extends App {
  val system = ActorSystem("H2")
  val first = system.actorOf(Props[H2], name = "first")
  val second = system.actorOf(Props[H2], name = "second")
  val third = system.actorOf(Props[H2], name = "third")
  Paths.p1 = first.path.toString
  Paths.p2 = second.path.toString
  Paths.p3 = third.path.toString
  println(first.path)
  first ! Start("T", second.path.toString, third.path.toString)
   //first ! Send("T", first.path.toString, second.path.toString, third.path.toString) 
  println("Server IS  ready")
}
