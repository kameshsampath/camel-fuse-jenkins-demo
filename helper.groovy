#!/usr/bin/groovy

/*
 *
 *   Copyright (c) 2017 Kamesh Sampath<kamesh.sampath@hotmail.com>
 *
 *   Red Hat licenses this file to you under the Apache License, version
 *   2.0 (the "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *   implied.  See the License for the specific language governing
 *   permissions and limitations under the License.
 */

def projectVersion() {
  def project = mavenProject()
  return project.version.text()
}

def fuseMavenBundle() {

  def project = mavenProject()

  String groupId = project.groupId.text()

  String artifactId = project.artifactId.text()

  return "mvn:" + groupId + "/" + artifactId + "/" + projectVersion();
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

  def version = "0.9.99" // if the object is released for first time

  try {
    def modelMetaData = new XmlSlurper().parse("${nexusUrl}/content/repositories/releases/${artifact}/maven-metadata.xml")
    if (modelMetaData) {
      version = modelMetaData.versioning.release.text()
    }
  } catch (FileNotFoundException e) {

  }
  return version
}

def tagVersion() {

  def tokens = projectVersion().tokenize('.')

  def major = tokens[0]
  def minor = tokens[1]
  def micro = tokens[2]

//  //we will go up to 99
//  if (micro.toInteger() < 99) {
//    micro = micro.toInteger() + 1
//  } else {
//    micro = "0"
//    if (minor.toInteger() < 9) {
//      minor = minor.toInteger() + 1
//    } else {
//      //Check this what maximum allowed for major
//      major = major.toInteger() + 1
//      minor = "0"
//    }
//  }
  echo "New version is ${major}.${minor}.${micro}"
  return "${major}.${minor}.${micro}"
}

def mavenProject() {
  def file = new File("${env['WORKSPACE']}/pom.xml").getText('UTF-8')
  def project = new XmlSlurper().parseText(file)
  return project
}

//println tagVersion()
return this
