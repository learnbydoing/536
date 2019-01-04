import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorRef, ActorLogging, ActorSystem, Props, AddressFromURIString}
import akka.remote.routing.RemoteRouterConfig
import akka.routing.RoundRobinPool
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import akka.routing.ConsistentHashingPool
import akka.routing.{Broadcast, RoundRobinPool}

class ClientActor extends Actor{
	
	val master = context.actorOf(Props[MasterActor], name="master")
	
	def receive = {
	
	case START =>
     master ! SendInfo("The Pickwick Papers", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg580.txt")
     master ! SendInfo("Life And Adventures Of Martin Chuzzlewit", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg968.txt")
     master ! SendInfo("Hunted Down", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg807.txt")
     master ! SendInfo("Great Expectations", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg1400.txt")
     master ! SendInfo("A Tale of Two Cities", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg98.txt")
     master ! SendInfo("The Cricket on the Hearth", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg20795.txt")
     master ! SendInfo("Bleak House", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg1023.txt")
     master ! SendInfo("Our Mutual Friend", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg883.txt")
     master ! SendInfo("Dombey and Son", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenbergs/pg821.txt")
     master ! SendInfo("Oliver Twist", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg730.txt")
     master ! SendInfo("The Old Curiosity Shop", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg700.txt")
     master ! SendInfo("David Copperfield", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg766.txt")
     master ! SendInfo("The Life And Adventures Of Nicholas Nickleby", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg967.txt")
     master ! SendInfo("A Child's History of England", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg699.txt")
     master ! SendInfo("A Christmas Carol", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg19337.txt")
     master ! SendInfo("Little Dorrit", "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg963.txt")
     master ! Flush
   }  
}//End class ClientActor
