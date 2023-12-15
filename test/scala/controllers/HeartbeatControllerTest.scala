package scala.controllers

import controllers.HeartbeatController
import org.scalatestplus.play._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

class HeartbeatControllerSpec extends PlaySpec with Results {

  "HeartbeatController #index" should {
    "should be valid" in {
      val controller  = new HeartbeatController(Helpers.stubControllerComponents())
      val result      = controller.index().apply(FakeRequest())
      val bodyText    = contentAsString(result)
      bodyText mustBe "ok"
    }
  }
}

