package scala.config

import cats.instances.all._
import com.typesafe.config.ConfigFactory
import config.Config
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting
import play.api.{Application, Configuration}

import scala.util.Right

class ConfigTest extends PlaySpec with GuiceOneAppPerSuite with Injecting {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .loadConfig(Configuration(ConfigFactory.load("test.conf")))
      .build()

  "Config" should {
    "should read value for given key if key is present" in {
      val config = inject[Config]
      config.get("KAFKA_HOST") mustBe Right("localhost")
    }
  }

}
