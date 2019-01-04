package common

import scala.collection.mutable.HashMap
import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._


class ReduceActor[X, Y, U](redFn: (X, List[Y]) => (X,U)) extends Actor {
  println(self.path)

  var remainingMappers = ConfigFactory.load.getInt("number-mappers")
  val aggOut = HashMap[X, List[Y]]()
  val redOut = HashMap[X, U]()

  def receive = {
  
     case ReduceIt(k: X, v: List[Y]) =>
        var (key, value) = (redFn(k,v))
        redOut += (key -> value)

    case Flush =>
      remainingMappers -= 1
      if (remainingMappers == 0) {
        println("\n*******************  PRINTING REDUCE MAP ********** " + self.path.toStringWithoutAddress + " : " + redOut)
        context.actorSelection("../..") ! Done
        context stop self
      }
  }
}
