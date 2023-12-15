package config

import cats._
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId}
import play.api.ConfigLoader.stringLoader
import play.api.Configuration

import javax.inject.Inject
import scala.util.{Failure, Success, Try}

class Config @Inject()(config: Configuration)  {
  def get[F[_]: ApplicativeError[*[_], String]](key: String): F[String] = {
    Try {
      config.get(key)
    } match {
      case Success(value) => value.pure[F]
      case Failure(exception) => s"Error while reading configuration."
                                  .raiseError[F, String]
    }
  }
}