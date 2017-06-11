package models

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsValue, Json, Writes}
import slick.driver.{JdbcProfile, MySQLDriver}
import slick.driver.MySQLDriver.api._
import slick.jdbc.GetResult

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by julienleroy on 29.05.17.
  */

case class Dish(id: Long, name: String, img_url: String, keywords: String, score: Long, published: Boolean) {

}

class Dishes(tag: Tag) extends Table[Dish](tag, "dishes") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def img_url = column[String]("img_url")

  def keywords = column[String]("keywords")

  def score = column[Long]("score")

  def published = column[Boolean]("published")

  override def * =
    (id, name, img_url, keywords, score, published) <> ((Dish.apply _).tupled, Dish.unapply)
}

object Dish {

  implicit val getDishResult = GetResult(r => Dish(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))

  implicit val dishWrites: Writes[Dish] = new Writes[Dish] {
    override def writes(o: Dish): JsValue = Json.obj(
      "name" -> o.name,
      "image" -> o.img_url,
      "keywords" -> o.keywords,
      "score" -> o.score
    )
  }

}


class DishDAO @Inject()(implicit ec: ExecutionContext, dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val dishes = TableQuery[Dishes]


  def getDish: Future[Seq[Dish]] = {
    //dbConfig.db.run(sql"SELECT * FROM dishes".as[Dish])
    dbConfig.db.run(dishes.result)
  }

  def getDishId(id: Long): Future[Dish] = {
    dbConfig.db.run(dishes.filter(_.id === id).result.head)
  }

  def getDishesByScore: Future[Seq[Dish]] = {
    dbConfig.db.run(dishes.sortBy(_.score.desc).take(20).result)
  }

  def getRandDishes(): Future[Vector[Dish]] = {
    dbConfig.db.run(sql"SELECT * FROM dishes ORDER BY RAND() LIMIT 0,2".as[Dish]).map(d => {
      d.map(dishes => dishes)
    }
    )
  }

  def expected(elo1: Long, elo2: Long): Double = 1 / ( 1 + Math.pow(10, ( elo2 - elo1 ) / 400) )


  def new_elo(elo1: Long, elo2: Long, score: Long): Long =  {
    val k = 80
    val newElo = Math.rint(elo1 + k * ( score - expected(elo1, elo2))).toLong

    if (newElo < 300) 300
    else newElo
  }


  def incriseScore(leftId: Long, rightId: Long, score: Int): Future[Unit] = {

    val query = (for {
      leftS <- dishes.filter(_.id === leftId).map(_.score).result
      rightS <- dishes.filter(_.id === rightId).map(_.score).result
      leftNS = new_elo(leftS.head, rightS.head, score)
      rightNS = new_elo(rightS.head, leftS.head, 1 - score)

      updateLeft <- dishes.filter(_.id === leftId).map(_.score).update(leftNS)
      updateRight <- dishes.filter(_.id === rightId).map(_.score).update(rightNS)
    } yield ()).transactionally


    dbConfig.db.run(query)
  }

}






