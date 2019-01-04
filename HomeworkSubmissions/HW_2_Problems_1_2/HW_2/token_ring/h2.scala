//Urvi Patel
//CSC 536 - HW 2, Problem 2
//April 12, 2017

package HW2

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.ActorRef
import akka.actor.ActorSelection
import akka.actor.ActorPath

case class Start(left:ActorRef, right:ActorRef, number:Int )
case object TOKENA
case object TOKENB

class H2()  extends Actor {

   var left = self
   var right  = self  
   var actorId = 0
   var counterTokenA:Int = 0
   var counterTokenB:Int = 0

   def incA = { counterTokenA = counterTokenA + 1 }  
   def incB = { counterTokenB = counterTokenB + 1 }  

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
        left ! TOKENA
     } 

    case TOKENB => {
	Thread.sleep(500)
	this.incB
	println("TOKENB count for actor " + actorId + " is: " + counterTokenB)
        right ! TOKENB
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
  first ! TOKENB
  println("Server is  ready")
}
