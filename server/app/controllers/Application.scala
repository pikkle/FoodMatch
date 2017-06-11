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
      Ok(Json.obj("status" -> "ok", "deal" -> deal)).as("application/json")
    }.recover{case _ => BadRequest}
  }


  def postDeal(id: String): Action[AnyContent] = Action.async {
    request =>
      val side = request.body.asJson.map(json => (json \ "side").as[String]).getOrElse("null")

    dealDAO.getDeal(id).map{deals =>
      if (!deals.done) {
        dealDAO.voteDeal(id, side)
        Ok
      } else {
        BadRequest("Deal over")
      }
    }.recover{case _ => BadRequest}
  }

  def getLeaderboard: Action[AnyContent] = Action.async {
    dishesDAO.getDishesByScore.map{dishes =>
      Ok(Json.toJson(dishes)).as("application/json")
    }.recover{case _ => BadRequest}
  }

  def getAllDishes: Action[AnyContent] = Action.async {
    dishesDAO.getDish.map{dishes =>
      Ok(Json.toJson(dishes)).as("application/json")
    }.recover{case _ => BadRequest}

  }

  def getDish(id: String): Action[AnyContent] =  Action.async {
    dishesDAO.getDishId(id.toInt).map{dishes =>
      Ok(Json.toJson(dishes)).as("application/json")
    }.recover{case _ => BadRequest}
  }
}