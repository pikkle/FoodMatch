package webapp

import org.scalajs.dom
import org.scalajs.dom.raw.HTMLImageElement

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.JSApp
import ApiController._
import org.scalajs.dom.html.{Div, Title}

import scala.scalajs.js.annotation.JSExportTopLevel

object FoodMatchApp extends JSApp {
	var voted: Boolean = false
	var deal: Deal = _

	@JSExportTopLevel("initMatch")
	def initMatch(): Unit = {
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

	@JSExportTopLevel("initLeaderboard")
	def initLeaderboard(): Unit = {
		val container = dom.document.getElementById("leaderboard-container")
		val dishExample = dom.document.getElementById("leaderboard-example")

		fetchLeaderboard().map(leaderboard => {
			var i = 1
			leaderboard.foreach(dish => {
				val div = dishExample.cloneNode(true).asInstanceOf[Div]
				div.id = s"leaderboard-$i"
				//div.childNodes(0).asInstanceOf[Div].childNodes(0).asInstanceOf[HTMLImageElement].src = dish.imageUrl
				div.getElementsByTagName("img")(0).asInstanceOf[HTMLImageElement].src = dish.imageUrl
				div.getElementsByTagName("h5")(0).textContent = dish.title
				div.getElementsByTagName("p")(0).textContent = dish.keywords
				container.appendChild(div)
				i += 1
			})
		})
	}

	def changeDeal(): Unit = {
		val leftTitle = dom.document.getElementById("left-dish-title")
		val rightTitle = dom.document.getElementById("right-dish-title")

		val leftKeywords = dom.document.getElementById("left-dish-keywords")
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
			postChoice(choice, deal.uid).map(_ => changeDeal())
		}
	}

	@JSExportTopLevel("chooseLeft")
	def chooseLeft(): Unit = choose("left", "right-image")

	@JSExportTopLevel("chooseRight")
	def chooseRight(): Unit = choose("right", "left-image")

	override def main(): Unit = {}
}
