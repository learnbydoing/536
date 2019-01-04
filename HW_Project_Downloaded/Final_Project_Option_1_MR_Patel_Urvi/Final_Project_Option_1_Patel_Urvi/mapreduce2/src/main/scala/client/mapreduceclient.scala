package client

import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Props}
import scala.io.Source
import util.Random

import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet

import common._

object MapReduceClient extends App {

  val system = ActorSystem("MapReduceClient", ConfigFactory.load.getConfig("client"))
  var inputA = HashMap[String, String]()
  var inputB = HashMap[String, String]()
  var inputC = HashMap[String, String]()
  
  var helper = new Helper
  inputA = helper.createInputA
  inputB = helper.createInputB
  inputC = helper.createInputC
  
   
   val master1 = system.actorOf(Props(classOf[MasterActor[String,String,String,Int,String]], inputA, helper.mapperA, helper.wordCountAgg, helper.reducerA), name = "master1")
   val master2 = system.actorOf(Props(classOf[MasterActor[String,String,String,String,String]], inputB, helper.mapperB, helper.capWordAgg, helper.reducerB), name = "master2")
   val master3 = system.actorOf(Props(classOf[MasterActor[String,String,String,String,Int]], inputC, helper.mapperC, helper.webReversalAgg, helper.reducerC), name = "master3")
 
    master3 ! MAPME
    master3 ! Flush
}