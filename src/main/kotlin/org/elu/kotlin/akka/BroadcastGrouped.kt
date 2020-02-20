package org.elu.kotlin.akka

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.BroadcastGroup

fun main() {
  val actorSystem = ActorSystem.create("broadcast-streaming")
  val loggerRef1 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "LoggerOne")
  val loggerRef2 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "LoggerTwo")
  val loggerRef3 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "LoggerThree")

  val routees = listOf(loggerRef1, loggerRef2, loggerRef3).map { it.path().toString() }
  val broadcastRef = actorSystem.actorOf(BroadcastGroup(routees).props(), "broadcaster")

  broadcastRef.tell(EventOne(), ActorRef.noSender())
  broadcastRef.tell(EventTwo(), ActorRef.noSender())

  Thread.sleep(1000)
  actorSystem.terminate()
}
