package controllers

import play.api._
import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def getName = Action {
    Ok("Jim")
  }

  def getDeal = Action {
    Ok("msg")
  }

  def postDeal(id: String) = Action {
    Ok(id)
  }

  def getLeaderboard = Action {
    Ok("msg")
  }

}