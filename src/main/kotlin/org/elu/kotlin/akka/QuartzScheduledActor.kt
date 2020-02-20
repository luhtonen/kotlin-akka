package org.elu.kotlin.akka

import akka.actor.AbstractLoggingActor
import akka.actor.ActorSystem
import akka.actor.ExtendedActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import com.typesafe.config.ConfigFactory
import java.time.LocalDateTime

class QuartzScheduledActor : AbstractLoggingActor() {
  override fun preStart() {
    super.preStart()
    val actorSystem = context.system as ExtendedActorSystem
    QuartzSchedulerExtension(actorSystem)
        .schedule(
          "HelloKotlin",
          self,
          "hello kotlin quartz"
        )
  }

  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(String::class.java) { log().info(it) }
          .build()
}

fun main() {
  val actorSystem = ActorSystem.create("quartz-scheduler", ConfigFactory.parseResources("schedulers.conf"))
  actorSystem.actorOf(Props.create(QuartzScheduledActor::class.java), "akka-quartz-scheduler")

  Thread.sleep(15000)
  println("shutting down actor system: ${LocalDateTime.now()}")
  actorSystem.terminate()
}
