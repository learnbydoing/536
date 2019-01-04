

import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorRef, ActorLogging, ActorSystem, Props, AddressFromURIString}
import akka.remote.routing.RemoteRouterConfig
import akka.routing.RoundRobinPool
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import akka.routing.ConsistentHashingPool

case class Message(name: String, title: String)

class ClientActor extends Actor{
	
	val numberMappers  = ConfigFactory.load.getInt("number-mappers")
    val numberReducers  = ConfigFactory.load.getInt("number-reducers")
    var pending = numberReducers
    
    val addresses = Seq(
    AddressFromURIString("akka.tcp://MapReduceSystem@127.0.0.1:2555"),
    AddressFromURIString("akka.tcp://MapReduceSystem@127.0.0.1:2554"))
    
  def hashMapping: ConsistentHashMapping = 
  {
  	 	case SendInfo(key, value) => key
  	 	case Flush => Flush
  }
  
   var reduceActors = List[ActorRef]()
   for (i <- 0 until numberReducers)
     reduceActors = context.actorOf(ConsistentHashingPool(2, hashMapping=hashMapping).props(Props[ReduceActor]), "router"+i) :: reduceActors
  
    val remoteMapper = context.actorOf(RemoteRouterConfig(RoundRobinPool(4), addresses).props(Props(classOf[MapActor], reduceActors)))
  
  
  def receive = {
     case  START =>
     println("In START in clientactor.scala")
     remoteMapper ! SendInfo("The Pickwick Papers", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg580.txt")
     remoteMapper ! SendInfo("Life And Adventures Of Martin Chuzzlewit", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg968.txt")
     remoteMapper ! SendInfo("Hunted Down", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg807.txt")
     remoteMapper ! SendInfo("Great Expectations", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg1400.txt")
     remoteMapper ! SendInfo("A Tale of Two Cities", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg98.txt")
     remoteMapper ! SendInfo("The Cricket on the Hearth", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg20795.txt")
     remoteMapper ! SendInfo("Bleak House", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg1023.txt")
     remoteMapper ! SendInfo("Our Mutual Friend", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg883.txt")
     remoteMapper ! SendInfo("Dombey and Son", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg821.txt")
     remoteMapper ! SendInfo("Oliver Twist", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg730.txt")
     remoteMapper ! SendInfo("The Old Curiosity Shop", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg700.txt")
     remoteMapper ! SendInfo("David Copperfield", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg766.txt")
     remoteMapper ! SendInfo("The Life And Adventures Of Nicholas Nickleby", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg967.txt")
     remoteMapper ! SendInfo("A Child's History of England", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg699.txt")
     remoteMapper ! SendInfo("A Christmas Carol", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg19337.txt")
     remoteMapper ! SendInfo("Little Dorrit", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg963.txt")
     remoteMapper ! Flush
  }
  

}//End class ClientActor
