package webapp

import org.scalajs.dom

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExportTopLevel

object FoodMatchApp extends JSApp {
	def main(): Unit = {
		dom.document.addEventListener("keypress", { (e0: dom.Event) =>
			val e = e0.asInstanceOf[dom.KeyboardEvent]
			e.key match {
				case "a" => chooseLeft()
				case "l" => chooseRight()
				case _ =>
			}
		}, false)
	}

	def postChoice(choice: String): Unit = {
		val ajax = new dom.XMLHttpRequest()
		ajax.open("GET", "https://jsonplaceholder.typicode.com/posts/1")
		ajax.onload = (e: dom.Event) => {
			if (ajax.status == 200) println(ajax.responseText) else println(s"ERROR WHILE FETCHING: $ajax.status")
		}
		ajax.send()
	}

	@JSExportTopLevel("chooseLeft")
	def chooseLeft(): Unit = {
		println("You voted for left")
		postChoice("")
	}

	@JSExportTopLevel("chooseRight")
	def chooseRight(): Unit = {
		println("You voted for right")
	}

}

