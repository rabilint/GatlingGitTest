package assertions

import io.gatling.core.Predef._
import io.gatling.commons.stats.assertion.Assertion
import config.BaseHelpers._

object AssertionsMap {

  private def p95(name: String) = details(name).responseTime.percentile3
  private def p99(name: String) = details(name).responseTime.percentile4

  val assertionsMap: Map[String, Seq[Assertion]] = Map(

    "capacity" -> Seq(
      global.successfulRequests.percent.gte(0),
      p95("S01_T01_Open_Home").lt(P95_LIMIT),
      p95("S01_T03_Open_Random_Table").lte(P95_LIMIT),
      p95("S01_T04_Add_Table_To_Cart").lte(P95_LIMIT),
      p95("S01_T09-02_Checkout").lt(P95_LIMIT)
    ),

    "stability" -> Seq(
      global.successfulRequests.percent.gte(99.9), // Майже 100% успіху
      p95("S01_T01_Open_Home").lt(P95_LIMIT),
      p95("S01_T09-02_Checkout").lt(P95_LIMIT)
    ),

    "performance" -> Seq(
      global.failedRequests.percent.lte(100 - SUCCESS_RATE),
      p99("S01_T01_Open_Home").lte(P99_LIMIT),
      p99("S01_T02_Open_Category_Tables").lte(P99_LIMIT),
      p95("S01_T03_Open_Random_Table").lte(P95_LIMIT),
      p95("S01_T04_Add_Table_To_Cart").lte(P95_LIMIT),
      p99("S01_T05_Open_Category_Chairs").lte(P99_LIMIT),
      p95("S01_T06_Open_Random_Chair").lte(P95_LIMIT),
      p95("S01_T07_Add_Chair_To_A_Cart").lte(P95_LIMIT),
      p95("S01_T08_Open_Cart").lte(P95_LIMIT),
      p99("S01_T09-01_Checkout").lte(P99_LIMIT),
      p95("S01_T09-02_Checkout").lte(P95_LIMIT)
    ),

    "stress" -> Seq(
      global.failedRequests.percent.lte(5.0),
      p95("S01_T01_Open_Home").lt(P95_LIMIT * 2),
      p95("S01_T03_Open_Random_Table").lte(P95_LIMIT * 2),
      p95("S01_T04_Add_Table_To_Cart").lte(P95_LIMIT * 2),
      p95("S01_T09-02_Checkout").lt(P95_LIMIT * 2)
    ),

    "response" -> Seq(
      global.failedRequests.percent.lte(0.1),
      global.responseTime.mean.lte(P95_LIMIT / 2),
      global.responseTime.stdDev.lte(250),
      global.requestsPerSec.gte(10),

      p95("S01_T01_Open_Home").lte(P95_LIMIT),
      p95("S01_T09-02_Checkout").lte(P95_LIMIT)
    )
  )
}
