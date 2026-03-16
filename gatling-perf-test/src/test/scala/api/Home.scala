package api

import config.BaseHelpers._
import io.gatling.core.Predef._
import io.gatling.core.structure._
import io.gatling.http.Predef._

object Home {
  def pteHome(): ChainBuilder = {
    group("S01_T01_Open_Home") {
      exec(
        http("S01_T01_Open_Home")
          .get("/")
          .check(regex("Main Categories").exists)
      )
    }
      .exec(thinkTimer(1,3))
  }
}
