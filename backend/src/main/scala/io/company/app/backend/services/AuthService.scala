package io.company.app.backend.services

import java.util.UUID
import com.avsystem.commons._
import io.company.app.shared.model.SharedExceptions
import io.company.app.shared.model.auth.{Permission, UserContext, UserToken}

import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

final class ExternalIdentityProvider {
  def auth(user: String, password: String): Future[UserContext] = {
    //call DB here, assuming all users auth for simplicity
    Future.successful(UserContext(
      UserToken(UUID.randomUUID().toString),
      "nameFromDb",
      Permission.values.map(_.id).toSet,
    ))
  }
}

class AuthService(identityProvider: ExternalIdentityProvider) {

  //could be a more complex cache with time-based invalidation etc.
  private val tokens: ConcurrentHashMap[UserToken, UserContext] = new ConcurrentHashMap[UserToken, UserContext]()

  /** Tries to authenticate user with provided credentials. */
  def login(username: String, password: String): Future[UserContext] =
    identityProvider.auth(username, password)
      .andThenNow { case Success(ctx@UserContext(token, _, _)) => tokens.put(token, ctx) }
      .recoverWithNow {
        case t => throw SharedExceptions.UserNotFound()
      }

  def findUserCtx(userToken: UserToken): Option[UserContext] =
    tokens.get(userToken).option
}
