package org.elu.kotlin.akka

import akka.actor.AbstractLoggingActor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder

class LoggingActor : AbstractLoggingActor() {
  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(EventOne::class.java) { log().info("Received Event One") }
          .match(EventTwo::class.java) { log().info("Received Event Two") }
          .build()
}

fun main() {
  val actorSystem = ActorSystem.create("streaming")
  val loggerRef1 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger1")
  val loggerRef2 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger2")
  val loggerRef3 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger3")

  val eventStream = actorSystem.eventStream()
  eventStream.subscribe(loggerRef1, Event::class.java)
  eventStream.subscribe(loggerRef2, EventOne::class.java)
  eventStream.subscribe(loggerRef3, EventTwo::class.java)

  eventStream.publish(EventOne())
  eventStream.publish(EventTwo())

  Thread.sleep(1000)
  actorSystem.terminate()
}
