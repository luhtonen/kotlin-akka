plugins {
  kotlin("jvm") version "1.3.61"
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.61")
  implementation("com.typesafe.akka:akka-actor_2.13:2.6.3")
  implementation("com.typesafe.akka:akka-slf4j_2.13:2.6.3")
  implementation("com.typesafe.akka:akka-stream_2.13:2.6.3")
//  implementation("com.enragedginger:akka-quartz-scheduler_2.13:1.8.2-akka-2.6.x")
}

repositories {
  mavenCentral()
}
