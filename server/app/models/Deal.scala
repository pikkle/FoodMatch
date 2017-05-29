package models

import slick.driver.MySQLDriver.api._

/**
  * Created by julienleroy on 29.05.17.
  */
case class Deal (id: Long, uid: String, left: Long, right: Long, done: Boolean) {

}

class DealTableDef(tag: Tag) extends Table[Deal](tag, "deals") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def uid = column[String]("uid")
  def left = column[Long]("left")
  def right = column[Long]("right")
  def done = column[Boolean]("done")

  override def * =
    (id, uid, left, right, done) <>(Deal.tupled, Deal.unapply)
}