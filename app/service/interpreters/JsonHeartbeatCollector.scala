package service.interpreters

import cats.effect.{ExitCode, IO}
import com.google.inject.Inject
import domain.Heartbeat
import play.api.libs.json.JsValue
import repositories.interpreters.kafka.JsonRepository
import service.algebras.HeartbeatCollector

class JsonHeartbeatCollector @Inject() (implicit heartbeatRepository: JsonRepository)
  extends HeartbeatCollector[IO, JsValue, ExitCode] {
  override def save(heartbeat: Heartbeat[JsValue]): IO[ExitCode] = {
    heartbeatRepository.save(heartbeat)
  }
}
