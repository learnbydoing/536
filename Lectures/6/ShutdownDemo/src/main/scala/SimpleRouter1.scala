import akka.actor.{ActorSystem, Actor, Props}
import akka.event.Logging
import akka.routing.RoundRobinPool
import java.io.FileNotFoundException

case class Message(msg: String)


class SimpleActor extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case Message(msg) => log.info("Got a valid message: %s".format(msg))
    //case default => log.error("Got a message I don't understand.")
    case value: Int => 
	if(value =5) 
           throw new FileNotFoundException
  }
}


object SimpleRouter1 extends App {
  val system = ActorSystem("SimpleSystem")
  val simpleRouted = system.actorOf(Props[SimpleActor].withRouter(
                        RoundRobinPool(nrOfInstances = 5)
                     ), name = "simpleRoutedActor")

  for (n <- 1 to 10)  simpleRouted ! Message("Hello, Akka #%d!".format(n))
}
