import akka.actor.{ActorSystem, Actor, Props, PoisonPill, Terminated}
import akka.event.Logging
import akka.routing.{RoundRobinPool, Broadcast}

import java.io.FileNotFoundException
import java.net.UnknownHostException

import akka.actor.{Actor, ActorSystem, Props, OneForOneStrategy}
import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}

class RouterSupervisor extends Actor {
  val log = Logging(context.system, this)
	
     override val supervisorStrategy =  OneForOneStrategy() {
      	case _: FileNotFoundException =>
      	 println("******  Failed actor in FileNotFound: " + sender)
      	 Restart
      	 
      	 case _: UnknownHostException =>
      	 println("Failed actor in UnknownHost: " + sender)
      	 Restart
      }

  val simpleRouter = context.actorOf(Props[SimpleActor].withRouter(
                        RoundRobinPool(nrOfInstances = 5).withSupervisorStrategy(supervisorStrategy)
                     ), name = "simpleRoutedActor")

  context.watch(simpleRouter) // supervise router

  def receive = {
    case Terminated(corpse) =>
      if (corpse == simpleRouter) {
        log.warning("Received termination notification for '" + corpse + "'," +
          "is in our watch list. Terminating ActorSystem.")
        SimpleRouter3.system.terminate
      }
  }
   simpleRouter ! 5

  //simpleRouter ! Broadcast(Message("I will not buy this record, it is scratched!"))
  //for (n <- 1 to 10) simpleRouter ! Message("Hello, Akka #%d!".format(n))
  //simpleRouter ! Broadcast(PoisonPill)
  //simpleRouter ! Message("Hello? You're looking a little green around the gills...") // never gets read
}

object SimpleRouter3 extends App {
  val system = ActorSystem("SimpleSystem")
  val supervisor= system.actorOf(Props[RouterSupervisor], name="routerSupervisor")
}
