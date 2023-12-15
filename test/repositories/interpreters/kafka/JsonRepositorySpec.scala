package repositories.interpreters.kafka

import cats.effect.ExitCode
import cats.effect.unsafe.implicits.global
import domain.Heartbeat
import io.github.embeddedkafka.Codecs.stringDeserializer
import io.github.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Results
import play.api.test.Injecting

import scala.concurrent.duration._
class JsonRepositorySpec extends PlaySpec with Results with EmbeddedKafka with GuiceOneAppPerTest with Injecting {
  "Json Repository" should {
    "send data to kafka" in {
      implicit val config = EmbeddedKafkaConfig(zooKeeperPort = 2181, kafkaPort = 9092)
      withRunningKafka {
        type JsonRepositoryId = JsonRepository
        val repository        = inject[JsonRepository]
        val jsonText          = s"""{"userId":"user_990","movieId":"movie_111","duration":3097,"position":3000,"timestamp":"2021-10-26T13:27:01+0000"}""".stripMargin
        val heartbeatJson     = Json.parse(jsonText)
        val result            = repository.save(Heartbeat[JsValue](heartbeatJson))

        result.unsafeRunSync()  mustBe ExitCode.Success
       consumeFirstMessageFrom[String]("heartbeats", true, 10.seconds) mustBe jsonText
      }
    }
  }
}
