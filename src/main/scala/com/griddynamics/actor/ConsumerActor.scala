package com.griddynamics.actor

import akka.actor.{ActorRef, Actor}
import kafka.consumer.KafkaStream
import kafka.message.Message
import com.griddynamics.ConsumeNext

/**
 * @author avinogradov
 */
class ConsumerActor(val stream: KafkaStream[Message],
                    val producerRouter: ActorRef) extends Actor {

  val streamIterator = stream.iterator()

  protected def receive = {
    case ConsumeNext => {
      if (streamIterator.hasNext()) {
        producerRouter ! streamIterator.next()
      }
      self ! ConsumeNext
    }
  }
}

