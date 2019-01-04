package common

import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap
import scala.io.Source

import akka.actor.{Actor, ActorRef}
import akka.routing.Broadcast
import scala.reflect.ClassTag

class MapActor[K,V,X,Y](reduceActors: ActorRef, input:HashMap[K,V], mapFn: (K,V) => List[(X,Y)], agg: List[(X,Y)] => HashMap[X, List[Y]]) extends Actor {
  
  var rMap: HashMap[X, List[Y]] = HashMap()
    
  def run(inp: HashMap[K,V], mf: (K,V) => List[(X,Y)], aggr: List[(X,Y)] => HashMap[X, List[Y]]) =
  {
  	var a : List[List[(X, Y)]]  =  List()
    var flattened : List[(X, Y)]  =  List()
    for((k,v) <- inp)
    {
       var retVal = mf(k, v)
       a = a :+ retVal
    }
	flattened = a.flatten
	var redPrepOutput = aggr(flattened)
	redPrepOutput = aggr(flattened)
	rMap = redPrepOutput
	
	for( (k,v) <- redPrepOutput )
	{
	   reduceActors ! ReduceIt(k,v)
	}
    
  }
  
  
  def receive = {
  
    case MAPME =>
       run(input, mapFn, agg)

    case Flush => 
      reduceActors ! Broadcast(Flush)
  }  
}
