package domain

import cats.Show
import play.api.libs.json._

case class Heartbeat[T](data: T)

object Heartbeat {
  type HeartbeatJson                                       = Heartbeat[JsValue]
  implicit val jsonHeartBeatFormatter: Show[HeartbeatJson] = (heartbeat: HeartbeatJson) => Json.stringify(heartbeat.data)
}