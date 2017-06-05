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

def containerName = ""
try {
  containerName = CONTAINER_NAME
} catch (Throwable e) {
  containerName = "jenkins-demo" //set to name suiting your env
}

def profileName = ""
try {
  profileName = PROFILE_NAME
} catch (Throwable e) {
  profileName = "demo-camel" //set to name suiting your env
}

node {

  stage('Checkout'){
    git 'http://localhost:3000/gogsadmin/camel-fuse-jenkins-demo.git'
  } //end checkout

  def helper = load 'helper.groovy'

  def bundleInfo = helper.fuseMavenBundle()

  wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {

    stage('Build and Test'){
       sh "./mvnw -B clean install"
    } // end build and unit test


    stage('Integration Tests'){
      echo ' maven goals for integration tests will be run here'
    } //end integration tests

    stage('Staging'){
        sh "./mvnw -DskipTests deploy"
    } // end staging

    // This will use the SSH credentials with id fabric8-dev, that need to be configured first before releases
    stage('Fuse Dev Deploy') {

        sshagent (credentials: ['fabric8-dev']) {

        echo "Using Continer : $containerName"

        echo "Using Profile : $profileName"

        echo "Bundle : $bundleInfo"

        def exec = """
ssh -o StrictHostKeyChecking=no -p 8101 -l karaf localhost << 'EOF'
source 'https://raw.githubusercontent.com/kameshsampath/fuse-fabric-pipelines/master/profile_update.karaf'
profile_update $containerName $profileName $bundleInfo
EOF"""

        def fout  = sh script: exec, returnStdout: true
        println fout

        if(fout.contains("Error")) {
           currentBuild.result = 'FAIL'
        }
       }
    } //end stage fuse deploy

  } //end Ansicolor

  input "Promote ?"

  stage('Release'){
       //This is just to show one of the many ways to increment the build version via jenkins build
       sh './mvnw build-helper:parse-version versions:set -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} '
       def tagName = helper.tagVersion()
       sh "./mvnw  scm:tag -DtagName=${tagName}"
       sh "./mvnw clean -B"
       sh "./mvnw -V -B -U -DskipTests clean install deploy"

       // prepare next iteration
       sh './mvnw build-helper:parse-version versions:set -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} '
       def snapshotVersion = helper.projectVersion()
       sh "git commit -a -m '[CD] prepare for next development iteration ${snapshotVersion}'"
       sh "git push origin master"

  } //end release

}
