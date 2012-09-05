package com.griddynamics.actor

import akka.actor.Actor
import com.griddynamics.ProduceMessage
import kafka.producer._
import kafka.message.Message

/**
 * @author avinogradov
 */
class ProducerActor(val config: ProducerConfig) extends Actor {

  val kafkaProducer = new Producer[Null, Message](config)

  protected def receive = {
    case ProduceMessage(messageAndMetadata) => {
      val data = new ProducerData[Null, Message](messageAndMetadata.topic, messageAndMetadata.message)
      kafkaProducer.send(data)
    }
  }
}
