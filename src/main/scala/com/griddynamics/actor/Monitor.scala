package com.griddynamics.actor

import akka.actor.Actor
import com.griddynamics.{CheckProducing, Produced}
import org.joda.time.{Period, DateTime}
import akka.event.Logging
import akka.util.duration._

/**
 * @author avinogradov
 */
class Monitor extends Actor {

  context.system.scheduler.schedule(13.seconds, 800.millis, self, CheckProducing)

  val log = Logging(context.system, this)

  var sumProduced: Long = 0
  var lastProduced: Long = 0
  var startTime: Option[DateTime] = None

  protected def receive = {

    case Produced(size) => {
      if (sumProduced > 0) {
        sumProduced += size
      } else {
        startTime = Some(new DateTime)
        sumProduced += size
      }
    }

    case CheckProducing => {
      if (startTime != None && sumProduced == lastProduced) {
        val period = new Period(startTime.get, new DateTime)
        log.info("Produced {} bytes ({} bytes/s)", sumProduced, sumProduced/period.getSeconds)
        startTime = None
        sumProduced = 0
        lastProduced = 0
      } else {
        lastProduced = sumProduced
      }
    }
  }
}
