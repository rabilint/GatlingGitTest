package simulations
import assertions.AssertionsMap
import config.BaseHelpers._
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import scenarios.StoreScenario
import assertions.AssertionsMap._

import scala.concurrent.duration._

class PerfTestSimulations extends Simulation {

  // to run simulation write this:
  // mvn gatling:test "-Dgatling.simulationClass=simulations.PerfTestSimulations" "-Dusers=100" "-Dramp=10" "-Dduration=60" "-DbaseUrl=http://localhost"
  // Or with default values:
  // mvn gatling:test "-Dgatling.simulationClass=simulations.PerfTestSimulations"


  //opinion on preflight:
  //preflight requests (OPTIONS) are browser-generated requests for CORS checks.
  //in backend performance testing, we typically skip them because:
  // they don't generate significant load on the application logic/DB.
  // they create noise in the reports.
  // gatling acts as a client that doesn't need to enforce CORS browser security.


  val openModel: PopulationBuilder = StoreScenario.scnStore.inject(
    rampUsers(userCount).during(rampDuration.seconds)
  )

  val closedModel: PopulationBuilder = StoreScenario.scnStore.inject(
    constantConcurrentUsers(userCount).during(testDuration.seconds)
  )

 setUp(
  openModel
  ).protocols(httpProtocol)
    .assertions(assertions.AssertionsMap.assertionsMap(assertionType): _*);
 val capacityModel: PopulationBuilder = StoreScenario.scnStore.inject(
    rampConcurrentUsers(1).to(userCount).during(rampDuration.seconds)
  )

  val stabilityModel: PopulationBuilder = StoreScenario.scnStore.inject(
    rampConcurrentUsers(1).to(userCount).during(rampDuration.seconds),
    constantConcurrentUsers(userCount).during(testDuration.seconds)  // Тут буде 1800 сек
  )

 // setUp(
 //   stabilityModel
 // ).protocols(httpProtocol)
 // .assertions(AssertionsMap.assertionsMap(assertionType))

//  setUp(
  //   capacityModel
  // ).protocols(httpProtocol)
  //  .assertions(AssertionsMap.assertionsMap(assertionType))

}
