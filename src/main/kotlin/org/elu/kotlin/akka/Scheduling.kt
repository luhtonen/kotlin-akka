package org.elu.kotlin.akka

import akka.actor.AbstractLoggingActor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Cancellable
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder
import scala.concurrent.duration.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

data class ScheduledMessage(val message: String)

class ScheduledActor : AbstractLoggingActor() {
  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(ScheduledMessage::class.java) { schedule(it) }
          .match(String::class.java) { log().info(it) }
          .build()

  private fun schedule(message: ScheduledMessage) =
      context.system.scheduler()
          .scheduleOnce(
            Duration.create(100, TimeUnit.MILLISECONDS),
            self,
            message.message,
            context.system.dispatcher(),
            self
          )
}

class ScheduledCancellableActor : AbstractLoggingActor() {
  private lateinit var cancellable: Cancellable

  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(ScheduledMessage::class.java) { schedule(it) }
          .match(String::class.java) { log().info(it) }
          .build()

  private fun schedule(message: ScheduledMessage) {
    cancellable = context.system.scheduler()
        .schedule(
          Duration.create(0, TimeUnit.MILLISECONDS),
          Duration.create(100, TimeUnit.MILLISECONDS),
          self,
          message.message,
          context.system.dispatcher(),
          self
        )
  }
}

fun main() {
  val actorSystem = ActorSystem.create("scheduling")
  val repeatingScheduler = actorSystem.actorOf(Props.create(ScheduledActor::class.java), "once-off-scheduler")
  repeatingScheduler.tell(ScheduledMessage("hello kotlin ONCE"), ActorRef.noSender())
  Thread.sleep(200)

  val cancellableScheduler = actorSystem.actorOf(Props.create(ScheduledCancellableActor::class.java), "repeating-scheduler")
  cancellableScheduler.tell(ScheduledMessage("hello kotlin CANCELLABLE"), ActorRef.noSender())
  Thread.sleep(500)
  actorSystem.stop(cancellableScheduler)
  println("scheduler stopped: ${LocalDateTime.now()}")

  Thread.sleep(1000)
  println("shutting down actor system: ${LocalDateTime.now()}")
  actorSystem.terminate()
}
