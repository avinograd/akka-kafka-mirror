package com.griddynamics.kafka.mirror.actor

import akka.actor.Actor
import com.griddynamics.kafka.mirror.{CheckConsuming, Consumed, CheckProducing, Produced}
import org.joda.time.{Period, DateTime}
import akka.event.Logging
import akka.util.duration._

/**
 * @author avinogradov
 */
class Monitor extends Actor {

  context.system.scheduler.schedule(10.seconds, 5.seconds, self, CheckProducing)
  context.system.scheduler.schedule(10.seconds, 5.seconds, self, CheckConsuming)

  val log = Logging(context.system, this)

  var sumProduced: Long = 0
  var lastProduced: Long = 0
  var startProducing: Option[DateTime] = None

  var sumConsumed: Long = 0
  var lastConsumed: Long = 0
  var startConsuming: Option[DateTime] = None

  protected def receive = {

    case Produced(size) => {
      if (sumProduced > 0) {
        sumProduced += size
      } else {
        log.info("Start producing")
        startProducing = Some(new DateTime)
        sumProduced += size
      }
    }

    case CheckProducing => {
      if (startProducing != None && sumProduced == lastProduced) {
        val period = new Period(startProducing.get, new DateTime)
        log.info("Produced {} bytes ({} bytes/s)", sumProduced, sumProduced/period.getSeconds)
        startProducing = None
        sumProduced = 0
        lastProduced = 0
      } else {
        lastProduced = sumProduced
        log.info("Produced {} bytes", sumProduced)
      }
    }

    case Consumed(size) => {
      if (sumConsumed > 0) {
        sumConsumed += size
      } else {
        log.info("Start consuming")
        startConsuming = Some(new DateTime)
        sumConsumed += size
      }
    }

    case CheckConsuming => {
      if (startConsuming != None && sumConsumed == lastConsumed) {
        val period = new Period(startConsuming.get, new DateTime)
        log.info("Consumed {} bytes ({} bytes/s)", sumConsumed, sumConsumed/period.getSeconds)
        startConsuming = None
        sumConsumed = 0
        lastConsumed = 0
      } else {
        lastConsumed = sumConsumed
        log.info("Consumed {} bytes", sumConsumed)
      }
    }
  }
}
