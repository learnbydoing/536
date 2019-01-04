package tokenring;

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.ActorSelection
import akka.actor.Props

case class Start(nextPath: String, number: Int)
case object TOKEN

class RingActor extends Actor {
  var actor:Int = 0;
  var counter:Int = 0;
  var pathOfNext = "";
  def increment = { counter = counter + 1 }
  def receive = {
    case TOKEN =>
      this.increment
      println(actor + " Actor Token Received: " + counter)
      val next = context.actorSelection(pathOfNext)
      next ! TOKEN
    case Start(nextPath, number) => 
      actor = number
      pathOfNext = nextPath 
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
  first ! Start(second.path.toString, 1)
  second ! Start(third.path.toString, 2)
  third ! Start(first.path.toString, 3)
  first ! TOKEN
  println("Server Ready")
  var input = ""
  while(input == ""){
    input = System.console().readLine() 
  }
  system.shutdown
}
