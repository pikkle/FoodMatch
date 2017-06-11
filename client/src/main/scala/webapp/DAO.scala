package webapp

import derive.key

case class DealWrapperDAO(@key("status") status: String,
						  @key("deal") deal: DealDAO)

case class DealDAO(@key("uid") uid: String,
				   @key("left") left: Int,
				   @key("right") right: Int)

case class DishDAO(@key("name") title: String,
				  @key("image") image: String,
				  @key("keywords") keywords: String,
				  @key("score") score: Int)

