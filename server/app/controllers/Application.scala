package controllers


import com.google.inject.Inject
import models.{Dishes, DishDAO}
import play.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.mvc._
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.{ExecutionContext, Future}

class Application @Inject() (implicit ec: ExecutionContext, dishesDAO: DishDAO) extends Controller {



  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def getName = Action {
    Ok("Jim")
  }

  def getDeal = Action {
    Ok("test")
  }

  def postDeal(id: String) = Action {
    Ok(id)
  }

  def getLeaderboard = Action {
    Ok("msg")
  }

  def getAllDishes() = Action.async {
    dishesDAO.getDish().map{dishes =>
      Ok(Json.obj("status" -> "OK", "dishes" -> dishes)).as("application/json")
    }

  }

  def getDish(id: String)  =  Action {
    Ok("msg")
  }

}