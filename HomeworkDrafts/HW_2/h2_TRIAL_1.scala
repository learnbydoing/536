package second;

import akka.actor.{Actor, ActorSystem, Props}


class H2_TRIAL_1 extends Actor {
  def receive = {
    case "T" => 
      println("T from " + sender.path)
      Thread.sleep(500)
      if(sender.path.toString contains "first")
      {
        Thread.sleep(500)
        println("Inside first, sender.path is:" +  sender.path)
	println("First sends to second")
 	//second ! "T"
      }
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
    case "start" =>
      //val second = context.actorOf(Props[PingPong], name = "second")
      //val second = system.actorOf(Props[H2], name = "second")
      //val third = system.actorOf(Props[H2], name = "third")
      println(second.path)
      second ! "T"
  }
}

object hw2_TRIAL_1_Server extends App {
  val system = ActorSystem("H2_TRIAL_1")
  val first = system.actorOf(Props[H2_TRIAL_1], name = "first")
  val second = system.actorOf(Props[H2_TRIAL_1], name = "second")
  val third = system.actorOf(Props[H2_TRIAL_1], name = "third")
  println(first.path)
  first ! "start"
  println("Server IS  ready")
}
