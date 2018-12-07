def ciProject = 'labs-ci-cd'
def testProject = 'labs-test'
def devProject = 'labs-dev'
openshift.withCluster() {
    openshift.withProject() {
        ciProject = openshift.project()
        testProject = ciProject.replaceFirst(/^labs-ci-cd/, 'labs-test')
        devProject = ciProject.replaceFirst(/^labs-ci-cd/, 'labs-dev')
    }
}

pipeline {
    agent {
        label 'jenkins-slave-mvn'
    }
    environment {
        PROJECT_NAME = 'hr-partner'
        KUBERNETES_NAMESPACE = "${ciProject}"
    }
    stages {
        stage('Build, Quality, And Security') {
            parallel {
              stage('Build App') {
                  steps {
                      script {
                         def pom = readMavenPom file: 'pom.xml'
                         version = pom.version
                         {
                           try {
                             sh 'mvn install'
                           } catch (error) {
                             publishHTML(target: [
                               reportDir             : 'target',
                               reportFiles           : 'dependency-check-report.html',
                               reportName            : 'OWASP Dependency Check Report',
                               keepAll               : true,
                               alwaysLinkToLastBuild : true,
                               allowMissing          : true
                             ])
                             publishHTML([
                               allowMissing: true,
                               alwaysLinkToLastBuild: false,
                               keepAll: true,
                               reportDir: 'target/site/jacoco/',
                               reportFiles: 'index.html',
                               reportName: 'Jacoco Unit Test Report'
                             ])
                             zip  dir: 'target/site/jacoco/',
                                  glob: '',
                                  zipFile: 'target/site/jacoco/jacoco-unit-tests.zip',
                                  archive: false
                             emailext  to: 'kfrankli@redhat.com',
                                  attachmentsPattern: '**/*.zip',
                                  subject: "Pipeline Build ${currentBuild.fullDisplayName} Unit Test Reports",
                                  body: """Pipeline Build ${currentBuild.fullDisplayName} Unit Test Reports attached."""
                             throw error
                           }
                         }
                         def qualitygate = waitForQualityGate()
                         if (qualitygate.status != "OK") {
                             error "Pipeline aborted due to quality gate failure: ${qualitygate.status}"
                         }
                      }
                      publishHTML(target: [
                          reportDir             : 'target',
                          reportFiles           : 'dependency-check-report.html',
                          reportName            : 'OWASP Dependency Check Report',
                          keepAll               : true,
                          alwaysLinkToLastBuild : true,
                          allowMissing          : false
                      ])
                      publishHTML(target: [
                          reportDir             : 'target/site/jacoco/',
                          reportFiles           : 'index.html',
                          reportName            : 'Jacoco Unit Test Report',
                          allowMissing          : true,
                          alwaysLinkToLastBuild : false,
                          keepAll               : true
                      ])
                      sh "mkdir jacoco-tmp && cp -r target/site/jacoco jacoco-tmp && rm jacoco-tmp/jacoco/jacoco-resources/*.js"
                      zip  dir: 'target/site/jacoco/',
                           glob: '',
                           zipFile: 'jacoco-unit-test-report.zip',
                           archive: true
                      zip  dir: 'jacoco-tmp/jacoco/',
                           glob: '',
                           zipFile: 'jacoco-unit-test-report-no-js.zip',
                           archive: false
                  }
              }
            
          }
        }
        stage('Create Image Builder') {
           when {
              expression {
                 openshift.withCluster() {
                    openshift.withProject("${ciProject}") {
                      return !openshift.selector("bc", "hr-partner").exists();
                    }
                 }
              }
           }
           steps {
              script {
                 openshift.withCluster() {
                    openshift.withProject("${ciProject}") {
                      openshift.newBuild("--name=hr-partner", "--image-stream=redhat-openjdk18-openshift:1.2", "--binary=true")
                    }
                 }
              }
           }
        }
        stage('Build Image') {
           steps {
              sh "rm -rf ocp && mkdir -p ocp/deployments"
              sh "pwd && ls -la target "
              sh "cp target/hr-partner-*.jar ocp/deployments"
              script {
                 openshift.withCluster() {
                    openshift.withProject("${ciProject}") {
                       openshift.selector("bc", "hr-partner").startBuild("--from-dir=./ocp","--follow", "--wait=true")
                    }
                 }
              }
           }
        }
        stage('Promote to TEST') {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject("${ciProject}") {
                            openshift.tag("hr-partner:latest", "${testProject}/hr-partner:latest")
                        }
                    }
                    def buildUrl = env.BUILD_URL
                    buildUrl = buildUrl.replaceAll(/\n*/, '')

                    emailext  to: 'john.johnson@hq.dhs.gov,snayak@bcmcgroup.com,ncho@bcmcgroup.com,kfrankli@redhat.com',
                    subject: "ACTION REQUIRED: Promote ${currentBuild.fullDisplayName} from TEST to DEMO?",
                    body: """Successfully built and deployed ${currentBuild.fullDisplayName} to TEST, should this be promoted to DEMO?
INPUT Required:
${buildUrl}input/

If the above link does not contain "promote" and "abort" buttons, someone else has already approved or aborted the promotion

Please review the following before promoting:
 * OWASP Dependency Scanner Report:
      ${buildUrl}/OWASP_20Dependency_20Check_20Report/
 * JaCoCo Unit Test Report:
      ${buildUrl}/Jacoco_20Unit_20Test_20Report/
 * SonarQube reports:
      https://sonarqube-labs-ci-cd.apps.domino.rht-labs.com/dashboard?id=gov.dhs.nppd%3Ahr-partner"""
                }
            }
        }
        stage('Promote to DEMO') {
          input {
              message "Promote service to DEMO environment?"
              ok "PROMOTE"
          }
          steps {
              script {
                  openshift.withCluster() {
                      openshift.withProject("${ciProject}") {
                          openshift.tag("hr-partner:latest", "${devProject}/hr-partner:latest")
                      }
                  }
              }
          }
        }
    }
    post {
      failure {
        script {
          def buildUrl = env.BUILD_URL
          buildUrl = buildUrl.replaceAll(/\n*/, '')
          emailext to: 'john.johnson@hq.dhs.gov,snayak@bcmcgroup.com,ncho@bcmcgroup.com,kfrankli@redhat.com',
          subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
          attachLog: true,
          attachmentsPattern: 'target/dependency-check-report.html',
          body: """BUILD FAILED ${buildUrl}

Please see attached:
      Build Log (build.log)
      OWASP Dependency Scanner Report (dependency-check-report.html)

SonarQube reports reside at:
      https://sonarqube-labs-ci-cd.apps.domino.rht-labs.com/dashboard?id=gov.dhs.nppd%3Ahr-partner"""
        }
      }
      success {
        script {
          def buildUrl = env.BUILD_URL
          buildUrl = buildUrl.replaceAll(/\n*/, '')
          emailext to: 'john.johnson@hq.dhs.gov,snayak@bcmcgroup.com,ncho@bcmcgroup.com,kfrankli@redhat.com',
          subject: "Successful Pipeline Build Reports: ${currentBuild.fullDisplayName}",
          attachmentsPattern: 'target/dependency-check-report.html,jacoco-unit-test-report-no-js.zip',
          body: """Build worked ${buildUrl}

Please see attached:
      OWASP Dependency Scanner Report (dependency-check-report.html)
      JaCoCo Unit Test Report* (jacoco-unit-test-report-no-js.zip)
Reports also available at:
      OWASP Dependency Scanner Report:
         ${buildUrl}/OWASP_20Dependency_20Check_20Report/
      JaCoCo Unit Test Report:
         ${buildUrl}/Jacoco_20Unit_20Test_20Report/
      SonarQube reports reside at:
         https://sonarqube-labs-ci-cd.apps.domino.rht-labs.com/dashboard?id=gov.dhs.nppd%3Ahr-partner

* The JaCoCo Unit Test Report has been modified slightly to remove all JavaScript report formatting files; as most email servers will not allow the transmission of Zip attachments container JavaScript (*.js) files. The unmodified JaCoCo report is available at:
      ${buildUrl}/artifact/jacoco-unit-test-report.zip"""
        }
      }
    }
}
