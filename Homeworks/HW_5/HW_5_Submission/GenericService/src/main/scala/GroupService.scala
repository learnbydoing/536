import akka.actor.{Actor, ActorSystem, ActorRef, Props}
import akka.event.Logging

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout





class GenericCell(var actors : scala.collection.mutable.ListBuffer[ActorRef])
{
  //For debugging
  override def toString: String = {
       var ret = ""
       for(actor <- actors){
          ret += actor.toString
          }
          return ret
    }
}

/**
 * GenericService is an example app service for the actor-based KVStore/KVClient.
 * This one stores Generic Cell objects in the KVStore.  Each app server allocates new
 * GenericCells (allocCell), writes them, and reads them randomly with consistency
 * checking (touchCell).  The allocCell and touchCell commands use direct reads
 * and writes to bypass the client cache.  Keeps a running set of Stats for each burst.
 *
 * @param myNodeID sequence number of this actor/server in the app tier
 * @param numNodes total number of servers in the app tier
 * @param storeServers the ActorRefs of the KVStore servers
 * @param burstSize number of commands per burst
 */

case object MULTICAST

class GroupServer (val myNodeID: Int, val numNodes: Int, storeServers: Seq[ActorRef], burstSize: Int) extends Actor {
  val generator = new scala.util.Random
  val cellstore = new KVClient(storeServers)
  val dirtycells = new AnyMap
  val localWeight: Int = 70
  val log = Logging(context.system, this)

  var stats = new Stats
  var allocated: Int = 0
  var endpoints: Option[Seq[ActorRef]] = None
  
  var groupIds:List[BigInt] = Nil
  var localKeys:List[BigInt] = Nil
  

  def receive() = {
      case Prime() =>
         for(i <- 1 to 10)
         {
            allocGroup(i)
         }
         println("groupIds is: " + groupIds + "\tself is: " + self)
      case Command() =>
        statistics(sender)
        command
        //println("groupIds size is: " + groupIds.size)
        //println("groupIds is: " + groupIds)
      case View(e) =>
        endpoints = Some(e)
        //println("endpoints is: " + endpoints)
      case MULTICAST =>
        println(self + " Got message from group")
    }


 private def allocGroup(i:Int) = {
  	 //val key = chooseEmptyCell
  	 val key = cellstore.hashForKey(myNodeID, i+1)
  	 var cell = directRead(key)
     if(cell.isEmpty)
     {
         var r = new GenericCell(scala.collection.mutable.ListBuffer(self))
         println("Add to store: " + key + " : " + r + "\tself is: " + self)
         directWrite(key, r)
         groupIds = groupIds :+ key
         localKeys = localKeys :+ key
         println("GROUPIDs is: " + groupIds)  
     }
  }
  
   private def command() = 
   {
      val sample = generator.nextInt(100)
      if (sample < 33) 
      {
         println("JOIN")
         joinGroup
      }
      else if(sample >= 33 && sample < 66)
      {
      		println("LEAVE")
      		leaveGroup()
      }
      else if(sample >= 66 && sample < 100)
      {
          println("MULTICAST")
          multicastGroup()
      }
      
  }
  
  private def leaveGroup()
  {
      val index = generator.nextInt(localKeys.size)
	  val key = localKeys(index)
	  val cell = directRead(key)
      println("Cell  to LEAVE is: " + cell + "\tKey is: " + key)
      var v = cell.get
      var w = v.actors
      var x = w -= self
      var gc = new GenericCell(x)
      directWrite(key, gc)
      //println("w is: " + w)
     //println("REMOVE is: " + v)
  }
  
  private def multicastGroup()
  {
     val index = generator.nextInt(localKeys.size)
	 val key = localKeys(index)
	 val cell = directRead(key)
     println("Cell  to MULTICAST is: " + cell + "\tKey is: " + key)
     var v = cell.get
     var all = v.actors
     
     for(actor <- all)
     {
     	actor ! MULTICAST
     }
     
  }

  private def joinGroup()
  {
	val index = generator.nextInt(groupIds.size)
	val key = groupIds(index)
    val cell = directRead(key)
    var value = cell
    
    if(cell.isEmpty)
    {
        //println("Before joining EMPTY, cell is: " + cell + "\tsender is: " + sender)
    	var r = new GenericCell(scala.collection.mutable.ListBuffer(self))
    	directWrite(key, r)
    	val cellWritten = directRead(key)
    	//println("Appending to EMPTY cell, active cell is now: " + cellWritten + "\tKey is: " + key)
    	//println("After joining EMPTY, cell is: " + cell + "\tself is: " + self)
    }
    else
    {
        //println("Appending to NON EMPTY cell, active cell is: " + cell + "\tKey is: " + key)
        //println("Before joining NON EMPTY, cell is: " + cell + "\tself is: " + self)

        var v = cell.get
        var w = v.actors
        if(!w.contains(self))
        {
           var x = w :+ self
           var gc = new GenericCell(x)
           //println("var v is: " + v)
           directWrite(key, gc)
           val cRead = directRead(key)
           //println("After joining NON EMPTY, cell is: " + cRead + "\tself is: " + sender)
           
           if(!localKeys.contains(key))
    	      localKeys :+ key
        }
      }
    }
     
  private def statistics(master: ActorRef) = {
    stats.messages += 1
    if (stats.messages >= burstSize) {
      master ! BurstAck(myNodeID, stats)
      stats = new Stats
    }
  }
  

  private def chooseEmptyCell(): BigInt =
  {
    allocated = allocated + 1
    cellstore.hashForKey(myNodeID, allocated)
  }

  private def chooseActiveCell(): BigInt = {
    val chosenNodeID =
      if (generator.nextInt(100) <= localWeight)
        myNodeID
      else
        generator.nextInt(numNodes - 1)

    val cellSeq = generator.nextInt(allocated)
    cellstore.hashForKey(chosenNodeID, cellSeq)
  }

  //private def rwcheck(key: BigInt, value: GenericCell) = {
    //directWrite(key, value)
    //val returned = read(key)
    //if (returned.isEmpty)
      //println("rwcheck failed: empty read")
    //else if (returned.get.next != value.next)
      //println("rwcheck failed: next match")
    //else if (returned.get.prev != value.prev)
      //println("rwcheck failed: prev match")
    //else
      //println("rwcheck succeeded")
  //}

   private def read(key: BigInt): Option[GenericCell] = {
    val result = cellstore.read(key)
    if (result.isEmpty) None else
      Some(result.get.asInstanceOf[GenericCell])
  }

   private def write(key: BigInt, value: GenericCell, dirtyset: AnyMap): Option[GenericCell] = {
    val coercedMap: AnyMap = dirtyset.asInstanceOf[AnyMap]
    val result = cellstore.write(key, value, coercedMap)
    if (result.isEmpty) None else
      Some(result.get.asInstanceOf[GenericCell])
  }

   private def directRead(key: BigInt): Option[GenericCell] = {
    val result = cellstore.directRead(key)
    if (result.isEmpty) None else
      Some(result.get.asInstanceOf[GenericCell])
  }

  private def directWrite(key: BigInt, value: GenericCell): Option[GenericCell] = {
    val result = cellstore.directWrite(key, value)
    if (result.isEmpty) None else
      Some(result.get.asInstanceOf[GenericCell])
  }

  private def push(dirtyset: AnyMap) = {
    cellstore.push(dirtyset)
  }
}

object GroupServer {
  def props(myNodeID: Int, numNodes: Int, storeServers: Seq[ActorRef], burstSize: Int): Props = {
    Props(classOf[GroupServer], myNodeID, numNodes, storeServers, burstSize)
  }
}
