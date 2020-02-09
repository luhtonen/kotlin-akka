package org.elu.kotlin.akka

import akka.actor.AbstractLoggingActor
import akka.japi.pf.ReceiveBuilder

class HelloKotlinActor : AbstractLoggingActor() {
  override fun createReceive(): Receive =
      ReceiveBuilder().match(String::class.java) {
        log().info(it)
      }.build()
}
