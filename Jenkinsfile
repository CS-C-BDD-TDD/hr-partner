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
        MQ_BROKER_TCP = "humanreview-amq-tcp"
        MQ_PORT_TCP = "61616"
		MQ_PASSWORD = "Mprd2bnAMrcan!"
		MQ_USERNAME = "user"
    }
    stages {
        stage('Build, Quality, And Security') {
            parallel {
              stage('Build App') {
                  steps {
                      script {
                         def pom = readMavenPom file: 'pom.xml'
                         version = pom.version
                         sh 'mvn install'
                      }
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
        }
      }
      success {
        script {
          def buildUrl = env.BUILD_URL
          buildUrl = buildUrl.replaceAll(/\n*/, '')
        }
      }
    }
}
