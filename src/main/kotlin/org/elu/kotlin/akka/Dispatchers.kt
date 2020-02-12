package org.elu.kotlin.akka

import akka.actor.ActorRef.noSender
import akka.actor.ActorSystem
import akka.actor.Props
import com.typesafe.config.ConfigFactory

fun main() {
  val actorSystem = ActorSystem.create("dispatchers-example", ConfigFactory.parseResources("dispatchers.conf"))
  val actorRef1 = actorSystem.actorOf(Props.create(HelloKotlinActor::class.java),"actor1") // custom dispatcher
  val actorRef2 = actorSystem.actorOf(Props.create(HelloKotlinActor::class.java).withDispatcher("actor2-dispatcher"), "actor2") // pinned dispatcher
  val actorRef3 = actorSystem.actorOf(Props.create(HelloKotlinActor::class.java), "actor3") // default dispatcher
  actorSystem.log().info("Sending Hello Kotlin")
  actorRef1.tell("Hello Actor 1", noSender())
  actorRef2.tell("Hello Actor 2", noSender())
  actorRef3.tell("Hello Actor 3", noSender())

  Thread.sleep(100)
  actorSystem.terminate()
}
