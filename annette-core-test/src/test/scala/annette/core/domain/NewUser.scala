package annette.core.domain
import java.time.ZonedDateTime
import java.util.UUID

import akka.testkit.TestKit
import annette.core.domain.tenancy.UserManager
import annette.core.domain.tenancy.model.{ CreateUser, User }
import annette.core.security.verification.VerificationBus
import annette.core.test.PersistenceSpec

import scala.util.Random

trait NewUser { _: PersistenceSpec with TestKit =>

  private val random = new Random()

  def newUserActor() = {
    val uuid = UUID.randomUUID().toString
    system.actorOf(UserManager.props(verificationBus = new VerificationBus), s"user-$uuid")
  }

  def newCreateUser(id: UUID = UUID.randomUUID(), email: Option[String] = None, phone: Option[String] = None, login: Option[String] = None) = {
    CreateUser(
      username = login,
      displayName = None,
      firstName = generateString(),
      lastName = generateString(),
      middleName = genStrOpt,
      gender = genStrOpt,
      email = email,
      url = None,
      description = None,
      phone = phone,
      language = genStrOpt,
      roles = Map.empty,
      password = generatePassword,
      avatarUrl = None,
      sphere = None,
      company = None,
      position = None,
      rank = None,
      additionalTel = None,
      additionalMail = None,
      meta = Map.empty,
      status = 1)
  }
}
