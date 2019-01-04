import scala.collection.mutable.HashMap
import akka.actor.{Actor, ActorRef}
import com.typesafe.config.ConfigFactory


class ReduceActor extends Actor {
  var remainingMappers = ConfigFactory.load.getInt("number-mappers")
  var reduceMap = HashMap[String, List[String]]()

  def receive = {
    case SendInfo(word, title) =>
      if (reduceMap.contains(word)) 
      {
		reduceMap += (word -> (reduceMap(word):+(title)))
		//println("reduceMap is: " + reduceMap)
      }
      else
      {
		reduceMap += (word -> List(title))
    	//println("reduceMap is: " + reduceMap)
      }
	println("reduceMap is: " + reduceMap)
	
    case Flush =>
      remainingMappers -= 1
      if (remainingMappers == 0) {
        println(self.path.toStringWithoutAddress + " : " + reduceMap)
        //context.actorSelection("..") ! Done
        context.parent ! Done
      }
  }
}
