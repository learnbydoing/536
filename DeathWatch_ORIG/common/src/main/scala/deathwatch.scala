package common

import akka.actor.{Actor, Terminated, Deploy, AddressFromURIString, ActorSystem, Props, OneForOneStrategy}
import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}
import akka.remote.RemoteScope

class Child extends Actor {
  var sum = 0
  Thread.sleep(10)
  System.out.println("Child created with sum = " + sum)

  def receive = {
    case value: Int => {
      sum += value
      System.out.println("Sum = " + sum)
      val div = 1/value // if value is 0, throw new ArithemticException
    }
    case FINISHED => context.actorSelection("..") ! KILL
    case "stateError" => throw new IllegalStateException("State problems")
    case _ => throw new IllegalArgumentException("Expect only integers")
  }
}

class Supervisor extends Actor {
  override val supervisorStrategy = OneForOneStrategy() {
    case _: IllegalArgumentException => Resume
    case _: ArithmeticException      => Stop
    case _: Exception                => Restart
  }

  val address = AddressFromURIString("akka.tcp://Server@127.0.0.1:2552")
  val child = context.actorOf(Props[Child].withDeploy(Deploy(scope = RemoteScope(address))))
  println(child.path)
  context.watch(child)
  val master = context.actorSelection("akka.tcp://Server@127.0.0.1:2552/user/masteractor")
  
  master ! CONNECT
  
  def receive = {
    case KILL => {
      println("Supervisor: terminating child.")
      context.stop(child)
    }
    case Terminated(child) => {
      println("Supervisor: child terminated.")
    }
    case any: AnyRef  => child ! any
  }

}

