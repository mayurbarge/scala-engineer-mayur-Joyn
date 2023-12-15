package config

import cats.instances.all._
import com.typesafe.config.ConfigFactory
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting
import play.api.{Application, Configuration}

import scala.util.Right

class ConfigSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .loadConfig(Configuration(ConfigFactory.load("test.conf")))
      .build()

  "Config" should {
    "should read value for given key if key is present" in {
      val config = inject[Config]
      config.get("kafka.host") mustBe Right("localhost")
    }

    "should store errors when there are issues while retrieving values from config" in {
      val config = inject[Config]
      config.get("NOT-DEFINED") mustBe Left("Error while reading configuration.")
    }
  }

}
