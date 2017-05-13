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
node {

   stage('Checkout'){
    git 'http://localhost:3000/gogsadmin/came-fuse-jenkins-demo.git'
   }

   def relUtil = load 'release.groovy'

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
      def bundleInfo = getMavenBundleInfo()
      def exec = """
      ssh -o StrictHostKeyChecking=no -p 8101 -l karaf localhost << 'EOF'
      source 'https://raw.githubusercontent.com/kameshsampath/fuse-fabric-pipelines/master/profile_update.karaf'
      profile_update $containerName $profileName $bundleInfo
      EOF"""
      wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
           def fout  = sh script: exec, returnStdout: true
           println fout

           if(fout.contains("Error")) {
              currentBuild.result = 'FAIL'
           }
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
