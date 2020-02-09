package org.elu.kotlin.akka

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props

fun main() {
  val actorSystem = ActorSystem.create("helloKotlin")

  val actorRef = actorSystem.actorOf(Props.create(HelloKotlinActor::class.java))

  actorSystem.log().info("Sending Hello Kotlin")

  actorRef.tell("Hello Kotlin", ActorRef.noSender())
}
