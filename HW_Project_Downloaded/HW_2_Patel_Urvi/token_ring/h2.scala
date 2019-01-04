//Urvi Patel
//CSC 536 - HW 2, Problem 3
//April 12, 2017

package HW2

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.ActorRef
import akka.actor.ActorSelection
import akka.actor.ActorPath
import scala.collection.mutable.{Queue, Map}

case class Start(left:ActorRef, right:ActorRef, number:Int )
case object TOKENA
case object TOKENB
case object STARTCL
case object MARKER

class H2()  extends Actor {

   var left = self 
   var right  = self 
   var actorId = 0
   var counterTokenA:Int = 0
   var counterTokenB:Int = 0
   var saved:Boolean = false
   var recording = Map[ActorRef, Queue[Any]]()
   var stopRecording = Map[ActorRef, Boolean]()
   var openChannels = 2

   object state {
      var countTOKENA:Int = 0 
      var countTOKENB:Int = 0 
   }

   def incA = { counterTokenA = counterTokenA + 1 }  
   def incB = { counterTokenB = counterTokenB + 1 } 
   //def printState(a:Int, b:Int) =  { println("State for " + self.path.toStringWithoutAddress + " is: (" + a + ", " + b  + ")") } 

  def receive = {

   /* From the assignment description:
	 1. One token goes: actor 1 --> actor 2 --> actor 3 --> actor 1 --> actor 2
	 2. The other goes: actor 1 --> actor 3 --> actor 2
	
      The code is calling the token in (1), TOKENA 
      and the one in (2), TOKENB	
   */

    case TOKENA => {   
	Thread.sleep(500)
	this.incA
	println("TOKENA count for actor " + actorId + " is: " + counterTokenA)
	if(saved && !stopRecording(sender))
        {
	  recording(sender).enqueue(TOKENA)
	  println("Recording app message(s) for TOKENB on: " + self + " ---> " + sender.path.toString + ": " +  TOKENA)
        }
        left ! TOKENA
     } 

    case TOKENB => {
	Thread.sleep(500)
	this.incB
	println("TOKENB count for actor " + actorId + " is: " + counterTokenB)
	if(saved && !stopRecording(sender))
        {
	  recording(sender).enqueue(TOKENB)
	  println("Recording app message(s) for TOKENB on: " + self + " ---> " + sender.path.toString + ": " +  TOKENB)
        }
        right ! TOKENB
     } 

    case STARTCL => {
	 saved = true
         this.state.countTOKENA = counterTokenA
	 this.state.countTOKENB = counterTokenB
	 println(self + " got STARTCL message. It's State is: (" + this.state.countTOKENA + ", " + this.state.countTOKENB + ")")
	 recording(left) = Queue[Any]()
	 recording(right) = Queue[Any]()
	 stopRecording(left) = false
	 stopRecording(right) = false
	 right ! MARKER
	 left ! MARKER
    } 

    case MARKER => {
      if(!saved)
      {
	 println(self + " got MARKER for first time from " + sender.path.toString + " It's State is: (" + this.state.countTOKENA + ", " + this.state.countTOKENB + ")")
	 saved = true
         this.state.countTOKENA = counterTokenA
	 this.state.countTOKENB = counterTokenB
	 recording(left) = Queue[Any]()
	 recording(right) = Queue[Any]()
	 stopRecording(left) = false
	 stopRecording(right) = false
         stopRecording(sender) = true
 	 println("openChannels for " + self + "is: " + openChannels)
	 openChannels = openChannels - 1
	 right ! MARKER
         left ! MARKER
      }
      else
      {
        stopRecording(sender) = true
	openChannels = openChannels - 1
	if(openChannels == 0) 
        {
	  saved = false
	  println("****Snapshot for " + self + "\n" + "State is: (" + this.state.countTOKENA + ", " + this.state.countTOKENB + ")" +  "\nOutstanding messages for " + left + ": "   + recording(left) + "\n" + "Outstanding messages for " + right + ": " +  recording(right))
        }
      }
    }

    case Start(l, r, actorNumber) => {
       left = l
       right = r
       actorId = actorNumber
      }
   }
}

object hw2Server extends App {
  val system = ActorSystem("H2")
  val first = system.actorOf(Props[H2], name = "first")
  val second = system.actorOf(Props[H2], name = "second")
  val third = system.actorOf(Props[H2], name = "third")
  
  first ! Start(second, third, 1)
  second ! Start(third, first, 2)
  third ! Start(first, second, 3)
  first ! TOKENA
  //first ! TOKENB
  
  Thread.sleep(2000)
  first ! STARTCL
  println("Server is  ready")
}
