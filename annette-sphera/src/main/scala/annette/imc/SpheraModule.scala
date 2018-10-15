package annette.imc

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.util.FastFuture
import annette.core.{ AnnetteHttpModule, BuildInfo, CoreModule }
import annette.core.security.SecurityDirectives
import annette.imc.http.SpheraApi
import annette.imc.user.model.UserRoled
import com.typesafe.config.Config
import javax.inject.{ Inject, Singleton }

import scala.concurrent.{ ExecutionContext, Future }

case class SpheraContext(
  system: ActorSystem,
  config: Config,
  fileStorageDir: String,
  getUserRoled: UUID => Future[Option[UserRoled]])

class SpheraModule extends AnnetteHttpModule {
  def getUserRoled(ec: ExecutionContext)(userId: UUID) = {
    //    implicit val e: ExecutionContext = ec
    //    for {
    //      x <- coreModule.userService.getUserById(userId)
    //      y <- coreModule.tenantUserRoleDao.getByIds("IMC", userId)
    //    } yield {
    //      x.map { user =>
    //        val roles = y.map(_.roles)
    //
    //        UserRoled(
    //          user.id,
    //          user.lastName,
    //          user.firstName,
    //          user.middleName.getOrElse(""),
    //          user.email,
    //          roles.exists(_.contains("admin")),
    //          roles.exists(_.contains("secretar")),
    //          roles.exists(_.contains("manager")),
    //          roles.exists(_.contains("chairman")),
    //          roles.exists(_.contains("expert")),
    //          roles.exists(_.contains("additional")))
    //      }
    //    }
    ???
  }

  override def init(): Future[Unit] = {
    FastFuture.successful {

    }
  }

  override def name: String = "annette-imc"

  override def routes = {
    val ctx = SpheraContext(coreModule.system, coreModule.config, "file-storage", getUserRoled(coreModule.system.dispatcher))
    val imcApi = new SpheraApi(coreModule, ctx, coreModule.annetteSecurityDirectives.authenticated)
    imcApi.routes
  }

  override def buildInfo = BuildInfo.toString

}