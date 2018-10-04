package annette.core.domain.tenancy

import akka.Done
import akka.actor.{ ActorRef, Props }
import akka.util.Timeout
import annette.core.domain.tenancy.model.User.Id
import annette.core.domain.tenancy.model._
import javax.inject.{ Inject, Named, Singleton }

import scala.concurrent.{ ExecutionContext, Future }
import akka.Done
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import annette.core.AnnetteMessage
import annette.core.akkaext.actor._
import annette.core.akkaext.http.PageRequest
import annette.core.domain.tenancy.model.User._
import annette.core.domain.tenancy.actor.{ UsersActor, UsersState }
import annette.core.domain.tenancy.model.User.Id
import annette.core.domain.tenancy.model.{ UpdateUser, User }
import annette.core.security.verification.VerificationBus
import javax.inject._

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration._

@Singleton
class UserManager @Inject() (@Named("CoreService") actor: ActorRef)(implicit c: ExecutionContext, t: Timeout) {
  def create(x: CreateUser): Future[User] =
    ask(actor, CreateUserCmd(x))
      .mapTo[CreateUserSuccess]
      .map(_.x)

  def update(x: UpdateUser): Future[Unit] = {
    for {
      f <- ask(actor, UpdateUserCmd(x))
    } yield {
      f match {
        case Done =>
        case m: AnnetteMessage => throw m.toException

      }
    }
  }

  def setPassword(userId: Id, password: String): Future[Boolean] = {
    for {
      f <- ask(actor, UpdatePasswordCmd(userId, password))
    } yield {
      f match {
        case Done => true
        case m: AnnetteMessage => throw m.toException
      }
    }
  }

  def delete(userId: Id): Future[Boolean] = {
    for {
      f <- ask(actor, DeleteUserCmd(userId))
    } yield {
      f match {
        case Done => true
        case m: AnnetteMessage => throw m.toException
      }
    }
  }

  def getById(id: Id): Future[Option[User]] =
    ask(actor, GetUserById(id))
      .mapTo[UserOpt]
      .map(_.maybeEntry)

  def listUsers: Future[List[User]] =
    ask(actor, ListUsers)
      .mapTo[UsersMap]
      .map(_.x.values.toList)

  def paginateListUsers(page: PageRequest): Future[PaginateUsersList] =
    ask(actor, PaginateListUsers(page))
      .mapTo[UsersList]
      .map(_.x)

  def getByLoginAndPassword(login: String, password: String): Future[Option[User]] =
    ask(actor, GetUserByLoginAndPassword(login, password))
      .mapTo[UserOpt]
      .map(_.maybeEntry)
}

object UserManager {
  def props(id: String, verificationBus: VerificationBus, state: UsersState = UsersState()) =
    Props(new UsersActor(
      id = id,
      verificationBus = verificationBus,
      initState = state))
}
