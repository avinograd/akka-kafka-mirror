package com.griddynamics.actor

import akka.actor.{ActorRef, Actor}
import com.griddynamics.{Produced, ProduceMessage}
import kafka.producer._
import kafka.message.Message

/**
 * @author avinogradov
 */
class ProducerActor(val config: ProducerConfig, val monitor: ActorRef) extends Actor {

  val kafkaProducer = new Producer[Null, Message](config)

  protected def receive = {
    case ProduceMessage(messageAndMetadata) => {
      val data = new ProducerData[Null, Message](messageAndMetadata.topic, messageAndMetadata.message)
      kafkaProducer.send(data)
      monitor ! Produced(messageAndMetadata.message.size)
    }
  }
}
