package snapshot;

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.ActorSelection
import akka.actor.Props

case class sib(id: Int, path: String)
case class Start(id: Int, nextSib: sib, previousSib: sib)
case class ACK(number: Int)
case class MARKER(number: Int)
case object TOKEN

class RingActor extends Actor {
  var actor:Int = 0;var counter:Int = 0;var AckCount:Int = 0;
  var nextNode:sib = new sib(0, "")
  var previousNode:sib = new sib(0, "")
  var recordingState:Boolean = false
  var toNotify:String = ""
  def increment = { counter = counter + 1 }
  def receive = {
    case TOKEN => 
      if(recordingState){
        println("Token in Transit to " + nextNode.id)
      }
      this.increment
      println(actor + " Actor Token Received: " + counter + ".")
      val next = context.actorSelection(nextNode.path)
      next ! TOKEN
    case MARKER(number) =>
      if(!recordingState){
        println("Actor " + actor + " received Marker from Actor " + number + ", started recording state.")
        recordingState = true
        toNotify = sender.path.toString
        if(number != nextNode.id){
          val next = context.actorSelection(nextNode.path)
          next ! MARKER(actor)
          AckCount = AckCount + 1
        }
        if(number != previousNode.id){
          val previous = context.actorSelection(previousNode.path)
          previous ! MARKER(actor)
          AckCount = AckCount + 1
        }
      }else{
        println("Actor " + actor + " currently recording state, marker received from " + number + ".")
        sender ! ACK(actor)   
      }
    case ACK(number) =>
      if(recordingState){
        println("Actor " + actor + " received Ack from Actor " + number.toString + ".")
        AckCount = AckCount - 1
        if(AckCount <= 0){
          println("All Acks Received, State is " + counter + ": Actor " + actor + ".")
          AckCount = 0
          recordingState = false
          val firstSender = context.actorSelection(toNotify)
          firstSender ! ACK(actor)
        }
      }
    case Start(id, nextSib, previousSib) => 
      actor = id
      nextNode  = nextSib
      previousNode = previousSib
  }
}

object Server extends App {
  val system = ActorSystem("RingActor")
  val first = system.actorOf(Props[RingActor], name = "first")
  println(first.path)
  val second = system.actorOf(Props[RingActor], name = "second")
  println(second.path)
  val third = system.actorOf(Props[RingActor], name = "third")
  println(third.path)
  val firstSib = new sib(1, first.path.toString)
  val secondSib = new sib(2, second.path.toString)
  val thirdSib = new sib(3, third.path.toString)
  first ! Start(1, secondSib, thirdSib)
  second ! Start(2, thirdSib, firstSib)
  third ! Start(3, firstSib, secondSib)
  first ! TOKEN
  println("Server Ready")
  var i = 0
  while(i <= 2){
    Thread.sleep(500)
    second ! MARKER(0)
    i = i + 1
    Thread.sleep(500)
    third ! MARKER(0)
    i = i + 1
  }
  system.shutdown
}

