resolvers += Classpaths.typesafeResolver

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

// need this to resolve http://jcenter.bintray.com/org/jenkins-ci/jenkins/1.26/
// which is used by plugin "org.kohsuke" % "github-api" % "1.68"
resolvers += "Bintray Jcenter" at "https://jcenter.bintray.com/"

//#sbt-multi-jvm
addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.3.8")
//#sbt-multi-jvm

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.2.0")
