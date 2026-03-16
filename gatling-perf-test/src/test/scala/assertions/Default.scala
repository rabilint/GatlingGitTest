package assertions

import io.gatling.core.Predef._

object Default {

  val defaultAssertions = Seq{
    global.successfulRequests.percent.gt(1.0)
  }
}
