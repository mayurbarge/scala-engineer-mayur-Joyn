package repositories.interpreters.kafka

import akka.actor.ActorSystem
import cats.Show
import cats.effect.{ExitCode, IO}
import com.google.inject.Inject
import com.typesafe.config.{Config, ConfigFactory}
import config.Constants._
import domain.Heartbeat
import fs2.{Stream, kafka}
import fs2.kafka._
import play.api.libs.json.JsValue
import repositories.algebras.HeartbeatRepository

import scala.util.hashing.MurmurHash3

trait KafkaConfig {
  val config: Config    = ConfigFactory.load(this.getClass.getClassLoader)
  val producerConfig    = config.getConfig(CONFIG_KAFKA_PRODUCER)
  val host: String      = config.getString(KAFKA_HOST)
  val port: String      = config.getString(KAFKA_PORT)
  val topic: String     = config.getString(KAFKA_TOPIC)

  val producerSettings  = ProducerSettings[IO, String, String]
                          .withBootstrapServers(s"$host:$port")
  val stream            = KafkaProducer.stream(producerSettings)
}

class JsonRepository @Inject()(implicit system: ActorSystem)
        extends HeartbeatRepository[Heartbeat, IO, JsValue, ExitCode]
        with KafkaConfig {

  override def save(data: Heartbeat[JsValue]) = {
    import Heartbeat._
    val serializedHeartbeat: String = Show[Heartbeat[JsValue]].show(data)
    val record: ProducerRecords[Unit, String, String] = ProducerRecords.one(
      kafka.ProducerRecord(topic, MurmurHash3.stringHash(serializedHeartbeat, MurmurHash3.stringSeed).toString, serializedHeartbeat))

    stream.flatMap(
      producer => {
        Stream(record).evalMap(producer.produce(_).flatten)
      })
      .compile
      .drain.as(ExitCode.Success)
  }
}
