import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

scalaVersion := "2.11.7"

scalacOptions ++= List(
  "-unchecked",
  "-deprecation",
  "-language:_",
  "-target:jvm-1.6",
  "-encoding", "UTF-8"
)

lazy val commonSettings = Seq(
  organization := "akka.codepot",
  version := "0.0.1",
  scalaVersion := "2.11.7"
)

lazy val multiJvmSettings = Seq(
  // make sure that MultiJvm test are compiled by the default test compilation
  compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
  // disable parallel tests
  parallelExecution in Test := false,
  // make sure that MultiJvm tests are executed by the default test target,
  // and combine the results from ordinary test and multi-jvm tests
  executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
    case (testResults, multiNodeResults) =>
      val overall =
        if (testResults.overall.id < multiNodeResults.overall.id)
          multiNodeResults.overall
        else
          testResults.overall
      Tests.Output(overall,
        testResults.events ++ multiNodeResults.events,
        testResults.summaries ++ multiNodeResults.summaries)
  }
)

commonSettings

SbtMultiJvm.multiJvmSettings

configs(MultiJvm)

libraryDependencies ++= Dependencies.all


fork in run := true