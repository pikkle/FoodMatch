package models

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsValue, Json, Writes}
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by julienleroy on 29.05.17.
  */

case class Dish(id: Long, name: String, score: Long, published: Boolean) {

}

class Dishes(tag: Tag) extends Table[Dish](tag, "dishes") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def score = column[Long]("score")

  def published = column[Boolean]("published")

  override def * =
    (id, name, score, published) <> ((Dish.apply _).tupled, Dish.unapply)
}


class DishDAO @Inject()(implicit ec: ExecutionContext, dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val dishes = TableQuery[Dishes]


  def getDish(): Future[Seq[Dish]] = {
    dbConfig.db.run(dishes.result)
  }

  def getDishId(id: Long): Future[Dish] = {
    dbConfig.db.run(dishes.filter(_.id === id).result.head)
  }

  def getDishesByScore: Future[Seq[Dish]] = {
    dbConfig.db.run(dishes.sortBy(_.score).result)
  }

  def incriseScore(id: Long) = {
    var currentScore = dbConfig.db.run(dishes.filter(_.id === id).map(dishes => dishes.score).result.head)
    currentScore.map(score => {
      dbConfig.db.run(dishes.filter(_.id === id).map(dishes => dishes.score).update(score + 3))
    })

  }

}

object Dish {

  implicit val dishWrites: Writes[Dish] = new Writes[Dish] {
    override def writes(o: Dish): JsValue = Json.obj(
      "name" -> o.name,
      "score" -> o.score
    )
  }

}




