package com.griddynamics

import actor.{ConsumerActor, ProducerActor}
import akka.actor._
import kafka.message.{MessageAndMetadata, Message}
import kafka.utils.Utils
import kafka.consumer._
import akka.routing.RoundRobinRouter
import kafka.producer.ProducerConfig
import org.slf4j.LoggerFactory

sealed trait MirrorMessage
case object ConsumeNext extends MirrorMessage
case class ProduceMessage(message: MessageAndMetadata[Message]) extends MirrorMessage

object AkkaKafkaMirror extends App {

  val log = LoggerFactory.getLogger(AkkaKafkaMirror.getClass)

  val system = ActorSystem("akka-kafka-mirror")
  log.info("Actor system was started")

  // read properties
  val numberOfConsumerStreams = 1
  val numberOfProducers = 100

  // create producers pool
  val kafkaProducerConfig = new ProducerConfig(Utils.loadProps("producer.properties"))
  val producerRouter = system.actorOf(Props(new ProducerActor(kafkaProducerConfig)).withRouter(RoundRobinRouter(numberOfProducers)))
  log.info("Producer router was created")
  // create kafka consumer with some number of streams
  val kafkaConsumerConfig = new ConsumerConfig(Utils.loadProps("consumer.properties"))
  val kafkaConsumer = Consumer.create(kafkaConsumerConfig)
  log.info("Kafka consumer was created")
  // create consumer actor for each of streams
  val consumerPool = kafkaConsumer.createMessageStreamsByFilter(Whitelist(".*"), numberOfConsumerStreams).map(
    stream => system.actorOf(Props(new ConsumerActor(stream, producerRouter)))
  )
  log.info("Consumer pool was created")
  // start consuming
  consumerPool.foreach(_ ! ConsumeNext)
  log.info("Consuming started...")

}
