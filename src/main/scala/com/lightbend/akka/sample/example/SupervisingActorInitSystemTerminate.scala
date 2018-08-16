package com.lightbend.akka.sample.example

import akka.actor.{Actor, ActorSystem, Props}

/**
  * Example of how to terminate system when actor is failing while initial phase (constructor)
  * Useful in service discovery case f.ex
**/

object FailingActor {
  def props(msg: String): Props = Props(new FailingActor(msg))
  def props(): Props = Props(new FailingActor())
}

class FailingActor(val msg: String) extends Actor {

  override def receive: Receive = {
    case x: String => println(s"I received ur message [$x]")
  }

  def this() {
    this("default message 42")
    try {
      throw new Exception("I failed while init!")
    } catch {
      case e: Throwable =>
        context.parent ! e
        throw e
    }
  }
}

class Application extends Actor {
  val supervisingActor1 = context.actorOf(FailingActor.props("Super message"), "failing-actor1")

  // will fail
  val supervisingActor2 = context.actorOf(FailingActor.props(), "failing-actor2")

  override def receive: Receive = {
    case e : Throwable => {
      println(e)
      context.system.terminate()
    }
    case x: String => {
      println("all is ok yet")
      supervisingActor1 ! x
    }
  }
}


object SupervisingActorSystemTerminate extends App {
  val system = ActorSystem("testSystem")

  val applicationActor = system.actorOf(Props[Application], "supervising-actor")
  applicationActor ! "yojojo"
}
