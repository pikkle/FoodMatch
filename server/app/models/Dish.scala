package models
import slick.driver.MySQLDriver.api._


/**
  * Created by julienleroy on 29.05.17.
  */

case class Dish (id: Long, name: String, score: Long, published: Boolean) {

}

class DishTableDef(tag: Tag) extends Table[Dish](tag, "dishs") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def name = column[String]("name")
  def score = column[Long]("score")
  def published = column[Boolean]("published")

  override def * =
    (id, name, score, published) <>(Dish.tupled, Dish.unapply)
}
