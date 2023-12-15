package domain

import cats.implicits.toShow
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json}
import play.api.test.Injecting

class HeartbeatSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {
  "Heartbeat domain" should {
    "be able to create text description from heartbeat json" in {
      val heartbeatText                 = s"""{"userId":"user_987","movieId":"movie_214","duration":3097,"position":3000,"timestamp":"2021-10-26T13:27:01+0000"}""".stripMargin
      val heartbeat: Heartbeat[JsValue] = Heartbeat[JsValue](Json.parse(heartbeatText))
      heartbeat.show                    mustBe heartbeatText
    }
  }

}
