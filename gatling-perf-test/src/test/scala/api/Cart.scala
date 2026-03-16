package api

import config.BaseHelpers._
import io.gatling.core.Predef._
import io.gatling.core.structure._
import io.gatling.http.Predef._

import scala.util.Random

object Cart {
  def addToCart(productIdVar: String, taskNumber: String, taskName: String): ChainBuilder = {
    group(s"S01_${taskNumber}_${taskName}") {
      exec { session =>
        val productId = session(productIdVar).as[String]
        val cartContent = session("cart_content").asOption[String].getOrElse("{}")
        val addCartData = s"current_product=$productId&cart_content=$cartContent&current_quantity=1"
        session.set("add_cart_data", addCartData)
      }
      .exec(http(s"S01_${taskNumber}_${taskName}")
        .post("/wp-admin/admin-ajax.php")
        .formParam("action", "ic_add_to_cart")
        .formParam("add_cart_data", "${add_cart_data}")
        .formParam("cart_widget", "0")
        .formParam("cart_container", "0")
      )
    }
  }

  def openCartPage() : ChainBuilder = {
    group("S01_T08_Open_Cart") {
      exec(
        http("S01_T08_Open_Cart")
          .get("/cart")
          .check(

            regex("""class="to_cart_submit button green-box ic-design" type="submit" value="Place an order"""").exists
          )
      )
    }
  }


  def getCheckout: ChainBuilder = {
    group("S01_T09-01_Checkout"){
      exec(
        http("S01_T09-01_Checkout")
          .post("/checkout")
          .check(
            regex("""name="cart_content" value='(.*?)'""").saveAs("cart_content"),
            regex("""name="total_net" value="(.*?)"""").saveAs("total_net"),
            regex("""value="([^"]+)" name="trans_id"""").saveAs("trans_id"),

            regex("""name="(product_price_.*?)" value="(.*?)"""").ofType[(String, String)].findAll               .saveAs("extracted_prices_list")
            //Збираємо усі product_price_id__ товарів та їх ціну за одиницю.
            //Формуємо з них мапу і передаємо їх в POST запит який оформлює замовлення.
          )
      )
        .exec { session =>
          val cartContent = session("cart_content").asOption[String].getOrElse("{}")
          val canPay = cartContent != "{}" && cartContent.nonEmpty

          val pricesList = session("extracted_prices_list").as[Seq[(String, String)]]
          val pricesMap = pricesList.toMap
          session
            .set("can_pay", canPay)
            .set("product_prices_map", pricesMap)

        }
    }
  }

  def selectCountry: ChainBuilder = {

    exec(
      http("S01_T09-02_Select_Country")
        .post("/wp-admin/admin-ajax.php")
        .formParam("action", "ic_state_dropdown")
        .formParam("country_code", "${p_country}")
        .formParam("state_code", "")
    )

  }

  def placeOrder(): ChainBuilder = {
    group("S01_T09-02_Checkout"){

      doIfOrElse(session => session("can_pay").as[Boolean]) {
        exec(selectCountry) // ф-к для вибору країни
          .exec(
            http("S01_T09_Checkout_Submit")
              .post("/checkout")
              .formParam("ic_formbuilder_redirect", "http://localhost/thank-you")
              .formParam("cart_content", "${cart_content}")

              .formParamMap("${product_prices_map}")

              .formParam("total_net", "${total_net}")
              .formParam("trans_id", "${trans_id}")
              .formParam("shipping", "order")
              .formParam("cart_type", "order")
              .formParam("cart_inside_header_1", "<b>BILLING ADDRESS</b>")

              .formParam("cart_company", "${p_company}")
              .formParam("cart_name", "${p_name}")
              .formParam("cart_address", "${p_address}")
              .formParam("cart_postal", "${p_postal}")
              .formParam("cart_city", "${p_city}")


              .formParam("cart_country", "${p_country}") //цей параметр має бути, навіть за наявності, ф-к selectCountry
                                                         //просто через те-що це різні post запити. Якщо я помиляюсь — прошу поправити мене.

              .formParam("cart_state", "")
              .formParam("cart_phone", "${p_phone}")
              .formParam("cart_email", "${p_email}")
              .formParam("cart_comment", "${p_comment}")

              .formParam("cart_inside_header_2", "<b>DELIVERY ADDRESS</b> (FILL ONLY IF DIFFERENT FROM THE BILLING ADDRESS)")
              .formParam("cart_submit", "Place Order")

              .check(regex("No products.").notExists)
              .check(regex("Place Order").notExists)
              .check(currentLocationRegex(".*thank-you"))
              .check(regex("Thank You").exists)
          )
        } {
        exec(
          http("Error_Empty_Cart_Detected")
            .get("/")
            .check(substring("Cart is empty, Checkout skipped").exists.name("Error: Cart is empty, Checkout skipped"))
        )
        }
    }
      .exec(thinkTimer(23, 45))
  }
}
