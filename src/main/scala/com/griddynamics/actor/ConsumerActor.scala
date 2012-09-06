package com.griddynamics.actor

import akka.actor.{ActorRef, Actor}
import kafka.consumer.KafkaStream
import kafka.message.{MessageAndMetadata, Message}
import com.griddynamics.{Consumed, ProduceMessage, ConsumeNext}

/**
 * @author avinogradov
 */
class ConsumerActor(val stream: KafkaStream[Message],
                    val producerRouter: ActorRef,
                    val monitor: ActorRef) extends Actor {

  val streamIterator = stream.iterator()

  protected def receive = {
    case ConsumeNext => {
      stream.map { messageAndMetadata =>
        producerRouter ! ProduceMessage(messageAndMetadata)
        monitor ! Consumed(messageAndMetadata.message.size)
      }
/*
      if (streamIterator.hasNext()) {
        producerRouter ! ProduceMessage(streamIterator.next())
      }
      self ! ConsumeNext
*/
    }
  }
}

