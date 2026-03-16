package scenarios

import api._
import config.BaseHelpers._
import io.gatling.core.Predef._
import io.gatling.core.structure._
import io.gatling.http.Predef._

object StoreScenario {
  def scnStore: ScenarioBuilder = {
    scenario("Scenario of user buying something")
      .feed(userFeeder)
      .exec(flushHttpCache)
      .exec(flushCookieJar)

      .exitBlockOnFail(
        exec(Home.pteHome())
          .exec(Category.openCategory("tables","tableProductUrl"))
          .exec(Products.openProduct("table", "tableProductUrl", "tableId"))
          .exec(Cart.addToCart("tableId", "T04", "Add_Table_To_Cart"))

          .randomSwitch(
            Chairs.toDouble -> exec(Category.openCategory("chairs", "chairProductUrl"))
              .exec(Products.openProduct("chair", "chairProductUrl", "chairId"))
              .exec(Cart.addToCart("chairId", "T07", "Add_Chair_To_A_Cart"))
          )

          .randomSwitch(
            Order.toDouble -> exec(Cart.openCartPage())
              .exec(Cart.getCheckout)
              .exec(Cart.placeOrder())
          )
      )
  }
}
