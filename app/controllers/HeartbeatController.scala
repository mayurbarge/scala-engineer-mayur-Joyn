package controllers

import cats.Applicative
import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO}
import domain.Heartbeat
import play.api.mvc._
import service.interpreters.JsonHeartbeatCollector

import javax.inject.Inject

class HeartbeatController @Inject()(val controllerComponents  : ControllerComponents,
                                        heartbeatCollector    : JsonHeartbeatCollector)
  extends BaseController {

  def heartbeat = Action { request =>
    implicit val io = Applicative[IO]

    request.body.asJson.map(json => {
      heartbeatCollector
        .save(Heartbeat(json)).unsafeRunSync()
    }) match {
      case Some(ExitCode(0)) => Ok("Heartbeat saved.")
      case _ => InternalServerError("Error while submitting heartbeat.")
    }
  }

  def index = Action {
    Ok("Ok")
  }
}