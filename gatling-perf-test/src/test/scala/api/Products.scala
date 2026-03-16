package api

import config.BaseHelpers._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._


object Products {
  def openProduct(categoryName: String, productURL: String, productID: String): ChainBuilder= {
    val taskNumber = if (categoryName == "table") "T03" else "T06"
    val taskName = if (categoryName == "table") "Open_Random_Table" else "Open_Random_Chair"
    group(s"S01_${taskNumber}_${taskName}") {
      exec(
        http(s"S01_${taskNumber}_${taskName}")
          .get(s"$${${productURL}}")
          .check(
            regex("Products").exists,
            regex("""name="current_product" value="(\d+)"""").find.optional.saveAs(productID),
            regex("""name="cart_content" value='(.*?)'""").optional.saveAs("cart_content")
          )

      )
    }
      .exec(thinkTimer(1,3))
  }
}
