package models

import slick.driver.MySQLDriver.api._

/**
  * Created by julienleroy on 29.05.17.
  */


case class Deal (id: Long, uid: String, left: Long, right: Long, done: Boolean)

class Deals(tag: Tag) extends Table[Deal](tag, "deals") {

  val dishes: TableQuery[Dishes] = TableQuery[Dishes]

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def uid = column[String]("uid")
  def left_id = column[Long]("left")
  def right_id = column[Long]("right")
  def done = column[Boolean]("done")

  def left = foreignKey("left_dish_fk", left_id, dishes)(_.id)
  def right = foreignKey("right_dish_fk", right_id, dishes)(_.id)

  override def * =
    (id, uid, left_id, right_id, done) <>(Deal.tupled, Deal.unapply)
}