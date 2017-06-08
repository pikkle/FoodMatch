package models

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsValue, Json, Writes}
import slick.backend.DatabaseConfig
import slick.driver.{JdbcProfile, MySQLDriver}
import slick.driver.MySQLDriver.api._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by julienleroy on 29.05.17.
  */


case class Deal (id: Long, uid: String, left: Long, right: Long, done: Boolean)

class Deals(tag: Tag) extends Table[Deal](tag, "deals") {

  val dishes = TableQuery[Dishes]

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def uid = column[String]("uid")
  def left_id = column[Long]("left_id")
  def right_id = column[Long]("right_id")
  def done = column[Boolean]("done")

  def left = foreignKey("left_dish_fk", left_id, dishes)(_.id)
  def right = foreignKey("right_dish_fk", right_id, dishes)(_.id)

  override def * =
    (id, uid, left_id, right_id, done) <>((Deal.apply _).tupled, Deal.unapply)
}

class DealDAO @Inject() (implicit ec: ExecutionContext, dbConfigProvider: DatabaseConfigProvider, dishDAO: DishDAO) {
  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  val deals: TableQuery[Deals] = TableQuery[Deals]


  val insertQuery: MySQLDriver.IntoInsertActionComposer[Deal, Deal] = deals returning deals.map(_.id) into ((deal, id) => deal.copy(id = id))

  def getNewDeal: Future[Deal] = {
    def uuid = java.util.UUID.randomUUID.toString
    val action = insertQuery += Deal(0, uuid, 1, 2, done = false)
    dbConfig.db.run(action)
  }

  def getDeal(id: String): Future[Deal] = {
    dbConfig.db.run(deals.filter(_.uid === id).result.head)
  }

  def voteDeal(id: String, side: String): Future[Any] = {
    dbConfig.db.run(deals.filter(_.uid === id).result.head).map{deals =>
      val right = deals.right
      val left = deals.left
      println(side)

      if (side == "left"){
        dishDAO.incriseScore(left)
      } else if (side == "right"){
        dishDAO.incriseScore(right)
      }
    }
    //dbConfig.db.run(deals.filter(_.uid === id).map(_.done).update(true))
  }

}

object Deal {
  implicit val dealWrites: Writes[Deal] = new Writes[Deal] {
    override def writes(o: Deal): JsValue = Json.obj(
      "uid" -> o.uid,
      "left" -> o.left,
      "right" -> o.right
    )
  }
}