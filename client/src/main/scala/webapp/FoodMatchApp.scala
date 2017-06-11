package webapp

import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.HTMLImageElement

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExportTopLevel
import upickle.default._

object FoodMatchApp extends JSApp {
	var voted: Boolean = false
	val HOST: String = "http://localhost:9000"
	var deal: Deal = _

	def main(): Unit = {
		dom.document.addEventListener(
			"keypress",
			(e0: dom.Event) => {
				val e = e0.asInstanceOf[dom.KeyboardEvent]
				e.key match {
					case "a" => chooseLeft()
					case "l" => chooseRight()
					case "r" => changeDeal()
					case _ =>
				}
			},
			useCapture = false)
		changeDeal()
	}

	def changeDeal(): Unit = {
		val leftTitle = dom.document.getElementById("left-dish-title")
		val rightTitle = dom.document.getElementById("right-dish-title")

		val leftKeywords = dom.document.getElementById("right-dish-keywords")
		val rightKeywords = dom.document.getElementById("right-dish-keywords")

		val leftImage = dom.document.getElementById("left-image").asInstanceOf[HTMLImageElement]
		val rightImage = dom.document.getElementById("right-image").asInstanceOf[HTMLImageElement]

		leftTitle.textContent = ""
		rightTitle.textContent = ""
		leftKeywords.textContent = ""
		rightKeywords.textContent = ""
		leftImage.src = ""
		rightImage.src = ""

		fetchDeal().map(deal => {
			this.deal = deal

			leftTitle.textContent = deal.left.title
			rightTitle.textContent = deal.right.title
			leftImage.src = deal.left.imageUrl
			rightImage.src = deal.right.imageUrl
			leftKeywords.textContent = deal.left.keywords
			rightKeywords.textContent = deal.right.keywords

			turnGrayOff("left-image")
			turnGrayOff("right-image")
			voted = false
		})
	}

	def fetchDeal(): Future[Deal] = Ajax.get(HOST + "/deal").flatMap(xhr => xhr.status match {
		case 200 => parseDealResponse(xhr.responseText)
		case _ => throw new NoSuchElementException(s"ERROR WHILE FETCHING: $xhr.status")
	})

	def fetchDish(dishId: Int): Future[Dish] = Ajax.get(HOST + "/dishes/" + dishId).flatMap(xhr => xhr.status match {
		case 200 => parseDishResponse(xhr.responseText)
		case _ => throw new NoSuchElementException(s"ERROR WHILE FETCHING: $xhr.status")
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

	def postChoice(choice: String): Unit = Ajax.post(HOST + "/deal/" + deal.uid, s"""{ "side": "$choice" }""").map(xhr => xhr.status match {
		case 200 => changeDeal()
		case _ =>
	})

	def turnGrayOn(imageId: String): Unit = {
		dom.document.getElementById(imageId).classList.add("grayscale")
	}

	def turnGrayOff(imageId: String): Unit = {
		dom.document.getElementById(imageId).classList.remove("grayscale")
	}

	def choose(choice: String, grayout: String): Unit = {
		if (!voted) {
			voted = true
			turnGrayOn(grayout)
			postChoice(choice)
		} else {

		}
	}

	@JSExportTopLevel("chooseLeft")
	def chooseLeft(): Unit = choose("left", "right-image")

	@JSExportTopLevel("chooseRight")
	def chooseRight(): Unit = choose("right", "left-image")

}

