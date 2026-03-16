package api

import config.BaseHelpers._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Category {
  def openCategory(categoryName: String, productURL: String): ChainBuilder = {
    val taskNumber = if (categoryName == "tables") "T02" else "T05"
    val taskName = if (categoryName == "tables") "Open_Category_Tables" else "Open_Category_Chairs"
    group(s"S01_${taskNumber}_${taskName}") {
      exec(
        http(s"S01_${taskNumber}_${taskName}")
          .get(s"/${categoryName}")
          .check(regex("""href="([^"]*/products/[^"]*)"""").findRandom.saveAs(productURL))
      )
    }
      .exec(thinkTimer(1,3))
  }
}
