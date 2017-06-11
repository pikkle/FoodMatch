package webapp

import org.scalajs.dom.ext.Ajax
import upickle.default.read

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ApiController {
	val HOST: String = "http://localhost:9000"
	val jsonHeaders: Map[String, String] = Map("content-type" -> "application/json")

	def fetchDeal(): Future[Deal] = Ajax.get(HOST + "/deal").flatMap(xhr => xhr.status match {
		case 200 => parseDealResponse(xhr.responseText)
		case _ => throw new RuntimeException(s"Error on HTTP connection on ${"GET " + HOST + "/deal"}: error code $xhr.status")
	})

	def fetchDish(dishId: Int): Future[Dish] = Ajax.get(HOST + "/dishes/" + dishId).flatMap(xhr => xhr.status match {
		case 200 => parseDishResponse(xhr.responseText)
		case _ => throw new RuntimeException(s"Error on HTTP connection on ${"GET " + HOST + "/dishes/" + dishId}: error code $xhr.status")
	})

	def parseDishResponse(dishJson: String): Future[Dish] = {
		val dao = read[DishDAO](dishJson)
		Future.successful(Dish(dao.title, dao.image, dao.keywords, dao.score))
	}

	def parseDealResponse(dealJson: String): Future[Deal] = {
		val dao = read[DealWrapperDAO](dealJson)
		fetchDish(dao.deal.left).flatMap(left =>
			fetchDish(dao.deal.right).map(right =>
				Deal(dao.deal.uid, left, right)
			)
		)
	}

	def postChoice(choice: String, dealUid: String): Future[Unit] =
		Ajax.post(HOST + "/deal/" + dealUid, s"""{"side":"$choice"}""", 1000, jsonHeaders).map(xhr => xhr.status match {
			case 200 => Future.successful()
			case _ => throw new RuntimeException(s"Error on HTTP connection on ${"POST " + HOST + "/deal/" + dealUid}: error code $xhr.status")
		})
}
