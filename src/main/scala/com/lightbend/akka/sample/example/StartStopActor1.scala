package com.lightbend.akka.sample.example

import akka.actor.{Actor, ActorSystem, Props}

class StartStopActor1 extends Actor {
  override def preStart(): Unit = {
    println("first started")
    context.actorOf(Props[StartStopActor2], "second")
  }
  override def postStop(): Unit = println("first stopped")

  override def receive: Receive = {
    case "stop" ⇒ context.stop(self)
  }
}
object StartStopActor1 extends App{
  val system = ActorSystem("testSystem")

  val first = system.actorOf(Props[StartStopActor1], "first")
  first ! "stop"
}