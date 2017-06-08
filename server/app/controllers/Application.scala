package controllers


import com.google.inject.Inject
import models.{DealDAO, DishDAO, Dishes}
import play.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.mvc._
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.{ExecutionContext, Future}

class Application @Inject() (implicit ec: ExecutionContext, dishesDAO: DishDAO, dealDAO: DealDAO) extends Controller {



  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def getDeal: Action[AnyContent] = Action.async {
    dealDAO.getNewDeal.map{deal=>
      Ok(Json.obj("status" -> "ok", "deal" -> deal)).as("spplication/json")
    }
  }


  def postDeal(id: String): Action[AnyContent] = Action.async {
    request =>
      val side = request.body.asJson.map(json => (json \ "side").as[String]).getOrElse("null")

    dealDAO.getDeal(id).map{deals =>
      if (!deals.done) {
        dealDAO.voteDeal(id, side)
        Ok(Json.obj("status" -> "ok"))
      } else {
        Ok(Json.obj("status" -> "not ok"))
      }
    }
  }

  def getLeaderboard: Action[AnyContent] = Action.async {
    dishesDAO.getDishesByScore.map{dishes =>
      Ok(Json.obj("status" -> "ok", "dishes" -> dishes)).as("application/json")
    }
  }

  def getAllDishes: Action[AnyContent] = Action.async {
    dishesDAO.getDish().map{dishes =>
      Ok(Json.obj("status" -> "OK", "dishes" -> dishes)).as("application/json")
    }

  }

  def getDish(id: String): Action[AnyContent] =  Action.async {
    dishesDAO.getDishId(id.toInt).map{dishes =>
      Ok(Json.obj("status" -> "OK", "dishes" -> dishes)).as("application/json")
    }
  }
}