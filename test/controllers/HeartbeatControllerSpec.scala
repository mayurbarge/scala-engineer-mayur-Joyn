package controllers

import cats.effect.{ExitCode, IO}
import domain.Heartbeat
import io.github.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import service.interpreters.JsonHeartbeatCollector

class HeartBeatControllerSpec extends PlaySpec with Results with Injecting with GuiceOneAppPerTest with EmbeddedKafka {

  "HeartbeatController #index" should {
    "should be valid" in {
      val controller  = inject[HeartbeatController]
      val result      = controller.index().apply(FakeRequest("GET", "/index"))
      val bodyText    = contentAsString(result)
      bodyText mustBe "Ok"
    }
  }

  "HeartbeatController #heartbeat" should {
    "should be saved to kafka if valid" in {
      implicit val config = EmbeddedKafkaConfig(zooKeeperPort = 2181, kafkaPort = 9092)
      withRunningKafka {
        new WithHeartbeatTestData {
          val request     = FakeRequest("POST", "/heartbeat").withJsonBody(heartbeatJson)
          val controller  = inject[HeartbeatController]
          val result      = controller.heartbeat().apply()(request)
          val bodyText    = contentAsString(result)
          bodyText        mustBe "Heartbeat saved."
          status(result)  mustBe 200
        }
      }
    }
  }

  "HeartbeatController #heartbeat" should {
    "should show internal server error when heartbeat could not be saved" in {
        new WithHeartbeatTestData {
          val mockHeartBeatCollector = mock[JsonHeartbeatCollector]
          when(mockHeartBeatCollector.save(Heartbeat(heartbeatJson))).thenReturn(IO(ExitCode(1)))
          val request       = FakeRequest("POST", "/heartbeat").withJsonBody(heartbeatJson)
          val controller    = new HeartbeatController(Helpers.stubControllerComponents(), mockHeartBeatCollector)
          val result        = controller.heartbeat().apply()(request)
          val bodyText      = contentAsString(result)
          bodyText          mustBe "Error while submitting heartbeat."
          status(result)    mustBe 500
        }
    }
  }

  trait WithHeartbeatTestData {
    val jsonText = s"""{"userId":"user_990","movieId":"movie_111","duration":3097,"position":3000,"timestamp":"2021-10-26T13:27:01+0000"}""".stripMargin
    val heartbeatJson = Json.parse(jsonText)
  }
}

