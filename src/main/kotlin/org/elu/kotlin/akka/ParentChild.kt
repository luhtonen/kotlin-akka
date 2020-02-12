package org.elu.kotlin.akka

import akka.actor.AbstractLoggingActor
import akka.actor.ActorInitializationException
import akka.actor.ActorKilledException
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.AllForOneStrategy
import akka.actor.DeathPactException
import akka.actor.Props
import akka.actor.SupervisorStrategy
import akka.japi.pf.ReceiveBuilder
import scala.concurrent.duration.Duration

class ChildActor : AbstractLoggingActor() {
  override fun preStart() {
    super.preStart()
    log().info("Starting Child Actor")
  }

  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(String::class.java, this::onMessage)
          .build()

  private fun onMessage(message: String) =
      when (message) {
        "DIE" -> throw Exception("DEAD")
        else -> log().info(message)
      }
}

class ParentActor : AbstractLoggingActor() {
  override fun supervisorStrategy() = AllForOneStrategy(-1, Duration.Inf()) {
    when (it) {
      is ActorInitializationException -> SupervisorStrategy.stop() as SupervisorStrategy.Directive
      is ActorKilledException -> SupervisorStrategy.stop() as SupervisorStrategy.Directive
      is DeathPactException -> SupervisorStrategy.stop() as SupervisorStrategy.Directive
      else -> SupervisorStrategy.restart() as SupervisorStrategy.Directive
    }
  }

  override fun preStart() {
    super.preStart()
    context.actorOf(Props.create(ChildActor::class.java), "child1")
    context.actorOf(Props.create(ChildActor::class.java), "child2")
    context.actorOf(Props.create(ChildActor::class.java), "child3")
  }

  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(String::class.java) { context.children.forEach { child -> child.tell(it, self()) } }
          .build()
}

fun main() {
  val actorSystem = ActorSystem.create("parent-child")
  val actorRef = actorSystem.actorOf(Props.create(ParentActor::class.java), "parent")
  actorSystem.log().info("Sending Hello Kotlin")
  actorRef.tell("Hello Kotlin", ActorRef.noSender())
  actorSystem.log().info("Sending DIE message to child1. We expect all child actors to restart")
  actorSystem.actorSelection("akka://part2/user/parent/child1").tell("DIE", ActorRef.noSender())

  Thread.sleep(100)
  actorSystem.terminate()
}
