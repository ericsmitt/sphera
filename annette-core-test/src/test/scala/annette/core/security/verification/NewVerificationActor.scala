package annette.core.security.verification

import akka.actor.ActorRef
import annette.core.akkaext.actor.ActorId
import annette.core.security.verification.Verification._
import annette.core.test.PersistenceSpec

import scala.concurrent.Future
import scala.concurrent.duration._

trait NewVerificationActor { _: PersistenceSpec =>
  def createVerification(a: ActorRef): Future[Verification] = {
    val verification = CreateVerification(
      code = generatePinString,
      duration = 1.second)
    ask(a, CreateVerificationCmd(verification))
      .mapTo[CreateVerificationSuccess]
      .map(_.x)
  }

  def deleteVerification(a: ActorRef, id: Verification.Id): Future[Any] =
    ask(a, DeleteVerificationCmd(id))

  def getVerification(a: ActorRef): Future[Verification] =
    ask(a, GetVerification).mapTo[VerificationOpt].map(_.x.get)

  def listVerifications(a: ActorRef): Future[Map[Verification.Id, Verification]] =
    ask(a, ListVerifications).mapTo[VerificationMap].map(_.x)

  def verify(a: ActorRef, id: Verification.Id, code: String): Future[Any] =
    ask(a, VerifyCmd(id, code))

  def newVerificationActor(id: ActorId = generateActorId, state: VerificationState = VerificationState.empty): Future[ActorRef] = Future {
    system.actorOf(VerificationActor.props(id, state), id.name)
  }
}
