package config

import io.gatling.core.Predef._
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.core.structure._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

object BaseHelpers {
  val appUrl: String = System.getProperty("baseUrl", "http://localhost")
  val MinTime = 2
  val MaxTime = 5
  val Order = 30
  val Chairs = 50

//   val userCount: Int = Integer.getInteger("users", 200).toInt
//   val rampDuration: Int = Integer.getInteger("ramp", 900).toInt
//   val testDuration: Int = Integer.getInteger("duration", 900).toInt
//   val assertionType: String = System.getProperty("assertionType", "capacity")
/*
  val userCount: Int = 35                  // Saturation point was 45, we use 40 for stability
  val rampDuration: Int = 600              // Warm-up phase: 10 minutes (in seconds)
  val testDuration: Int = 14400            // Main phase: 4 hours (in seconds)
  val assertionType: String = "stability"*/

  val userCount: Int = Integer.getInteger("users", 5).toInt
  val rampDuration: Int = Integer.getInteger("ramp", 1).toInt
  val testDuration: Int = Integer.getInteger("duration", 10).toInt
  val assertionType: String = System.getProperty("assertionType", "stability")

  // val userCount: Int = 39                // Saturation point was 45, we use 40 for stability
  // val rampDuration: Int = 300              // Warm-up phase: 10 minutes (in seconds)
  // val testDuration: Int = 1800            // Main phase: 4 hours (in seconds)
  // val assertionType: String = "response"

  val userFeeder: BatchableFeederBuilder[String]#F = csv("feeders/users.csv").circular

  val P95_LIMIT = 10000
  val P99_LIMIT = 30000
  val SUCCESS_RATE = 99.0

  val httpProtocol: HttpProtocolBuilder =
    http
      .baseUrl(appUrl)
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
        "Sec-CH-UA-Platform" -> "Windows",
        "Sec-CH-UA" -> "\"Google Chrome\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"",
        "Sec-CH-UA-Mobile" -> "?0",
        "Sec-Fetch-Mode" -> "navigate",
        "Upgrade-Insecure-Requests" -> "1",
        "User-Agent" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36"
      ))
      .inferHtmlResources(
        WhiteList(s"$appUrl/.*"),
        BlackList(".*\\.google-analytics\\.com.*", ".*\\.doubleclick\\.net.*")
      )



  def thinkTimer(Min : Int = MinTime, Max: Int = MaxTime): ChainBuilder = {
    pause(Min,Max)
  }
}
