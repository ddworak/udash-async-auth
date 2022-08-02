package io.company.app.frontend.views.login

import io.company.app.frontend.routing.{LoginPageState, RoutingState}
import io.company.app.frontend.services.{TranslationsService, UserContextService}
import io.udash._

/** Prepares model, view and presenter for demo view. */
class LoginPageViewFactory(
  userService: UserContextService,
  application: Application[RoutingState],
  translationsService: TranslationsService
) extends ViewFactory[LoginPageState.type] {
  import scala.concurrent.ExecutionContext.Implicits.global

  override def create(): (View, Presenter[LoginPageState.type]) = {
    // Main model of the view
    val model = ModelProperty(
      LoginPageModel("", "", false, Seq.empty)
    )
    val presenter = new LoginPagePresenter(model, userService, application)
    val view = new LoginPageView(model, presenter, translationsService)
    (view, presenter)
  }
}