package service.interpreters

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO}
import domain.Heartbeat
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Results
import repositories.interpreters.kafka.JsonRepository

class JsonHeartbeatCollectorSpec extends PlaySpec with Results with MockitoSugar {
  "Json Heartbeat Collector" should {
    "save heartbeat to kafka" in {
      implicit val mockJsonRepository: JsonRepository = mock[JsonRepository]

      val heartbeat = Heartbeat[JsValue](Json.parse(s"{}"))
      when(mockJsonRepository.save(heartbeat)).thenReturn(IO(ExitCode.Success))
      val jsonHeartbeatCollector  = new JsonHeartbeatCollector()
      val result: ExitCode        = jsonHeartbeatCollector
                                    .save(heartbeat)
                                    .unsafeRunSync()
      result                      mustBe(ExitCode.Success)
    }

    "return error when heartbeat could not be saved to kafka" in {
      implicit val mockJsonRepository: JsonRepository = mock[JsonRepository]

      val heartbeat = Heartbeat[JsValue](Json.parse(s"{}"))
      when(mockJsonRepository.save(heartbeat)).thenReturn(IO(ExitCode.Error))
      val jsonHeartbeatCollector  = new JsonHeartbeatCollector()
      val result: ExitCode        = jsonHeartbeatCollector
        .save(heartbeat)
        .unsafeRunSync()
      result                      mustBe(ExitCode.Error)
    }
  }

}
