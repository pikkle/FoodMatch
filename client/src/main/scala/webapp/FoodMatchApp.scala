package webapp

import org.scalajs.dom

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExportTopLevel

object FoodMatchApp extends JSApp {
	def main(): Unit = {
		dom.document.getElementById("submit").addEventListener("keyPressed", callback)
	}

	def callback(): Unit = {

	}

	@JSExportTopLevel("chooseLeft")
	def chooseLeft(): Unit = {
		println("You voted for left")
	}

	@JSExportTopLevel("chooseRight")
	def chooseRight(): Unit = {
		println("You voted for right")
	}

}

