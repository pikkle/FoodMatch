package webapp

case class Deal(uid: String, left: Dish, right: Dish)
case class Dish(title: String, imageUrl: String, keywords: String, score: Int)
