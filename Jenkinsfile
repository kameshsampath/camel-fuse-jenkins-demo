#!/usr/bin/groovy
/*
 *
 *   Copyright (c) 2016 Red Hat, Inc.
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

def relUtil = load 'release.groovy'

def containerName = ""
try {
  containerName = CONTAINER_NAME
} catch (Throwable e) {
  containerName = "root" //set to name suiting your env
}

def profileName = ""
try {
  profileName = PROFILE_NAME
} catch (Throwable e) {
  profileName = "demo-camel" //set to name suiting your env
}

def bundleInfo = relUtil.getMavenBundleInfo()

node {

  stage('Checkout'){
    git 'http://localhost:3000/gogsadmin/came-fuse-jenkins-demo.git'
  }

  wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {

       stage('Build and Test'){
        sh "./mvnw clean install"
       }


       stage('Integration Tests'){
        echo ' maven goals for integration tests will be run here'
       }

       stage('Stage'){
          sh "./mvnw -DskipTests deploy"
       }

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
       }

       input "Promote ?"

       stage('Release'){
           def tagName = relUtil.tagVersion()
           sh "./mvnw  scm:tag -DtagName=${tagName}"
          //TODO the actual release will be done by another jenkins build job
       }
   }
}
