#!/usr/bin/groovy

def projectVersion() {
  def project = mavenProject()
  return project.version.text();
}

def getMavenBundleInfo() {
  return "mvn:"+fuseMavenBundle() +"/"+projectVersion()
}

def fuseMavenBundle(){

  def project = mavenProject()

  String groupId = project.groupId.text()

  groupId = groupId.replace('.', '/')

  String artifactId = project.artifactId.text()

  return  groupId + "/" + artifactId;
}

def releasedVersion() {

  def project = mavenProject()

  String nexusUrl = project.properties['nexus.url']

  //println("Nexus URL: ${nexusUrl}")

  String groupId = project.groupId.text()

  groupId = groupId.replace('.', '/')

  //println("Group ID: ${groupId}")

  String artifactId = project.artifactId.text()

  String artifact = groupId + "/" + artifactId

  //println("Artifact: ${artifact}")

  def modelMetaData = new XmlSlurper().parse("${nexusUrl}/content/repositories/releases/${artifact}/maven-metadata.xml")

  def version = "0.9.99" // if the object is released for first time

  if (modelMetaData) {
    version = modelMetaData.versioning.release.text()
  }

  return version
}

def tagVersion() {

  def tokens = releasedVersion().tokenize('.')

  def major = tokens[0]
  def minor = tokens[1]
  def micro = tokens[2]

  //we will go up to 99
  if (micro.toInteger() < 99) {
    micro = micro.toInteger() + 1
  } else {
    micro = "0"
    if (minor.toInteger() < 9) {
      minor = minor.toInteger() + 1
    } else {
      //Check this what maximum allowed for major
      major = major.toInteger() + 1
      minor = "0"
    }
  }

  return "${major}.${minor}.${micro}"
}

def mavenProject() {
  def file = new File("${env.WORKSPACE}/pom.xml").getText('UTF-8')
  def project = new XmlSlurper().parseText(file)
  return project
}

return this
