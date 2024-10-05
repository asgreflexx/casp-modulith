//file:noinspection GroovyAssignabilityCheck
//noinspection GroovyUnusedAssignment
@Library('Common@develop') _
import casp.common.Environment
import casp.common.Service

pipeline {

    agent any

    environment {
        EXPECTED_BRANCH_NAME = 'develop'
        EXPECTED_RESULT = 'SUCCESS'
        CODACY_CREDENTIALS = credentials('codacy-token')
    }

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }

    tools {
        maven "Default"
    }

    stages {
        stage('Maven Build') {
            steps {
                script {
                    sh 'mvn -B clean verify'
                }
                jacoco changeBuildStatus: true,
                        exclusionPattern: createExclusionPattern(),
                        maximumBranchCoverage: '80',
                        maximumClassCoverage: '80',
                        maximumComplexityCoverage: '80',
                        maximumLineCoverage: '80',
                        maximumMethodCoverage: '80',
                        minimumBranchCoverage: '50',
                        minimumClassCoverage: '50',
                        minimumComplexityCoverage: '50',
                        minimumLineCoverage: '50',
                        minimumMethodCoverage: '50'
            }
        }

        stage('Upload coverage report to Codacy') {
            when {
                expression {
                    env.BRANCH_NAME == env.EXPECTED_BRANCH_NAME && currentBuild.currentResult == env.EXPECTED_RESULT
                }
            }
            steps {
                script {
                    sh """
                        export CODACY_PROJECT_TOKEN=${CODACY_CREDENTIALS_PSW}
                        bash <(curl -Ls https://coverage.codacy.com/get.sh) report -r target/site/jacoco/jacoco.xml
                    """
                }
            }
        }

        stage('Docker Build and Push') {
            when {
                expression {
                    env.BRANCH_NAME == env.EXPECTED_BRANCH_NAME && currentBuild.currentResult == env.EXPECTED_RESULT
                }
            }
            steps {
                buildImageAndPush(Service.ADMIN_V2)
            }
        }

        stage('Restart Service') {
            when {
                expression {
                    env.BRANCH_NAME == env.EXPECTED_BRANCH_NAME && currentBuild.currentResult == env.EXPECTED_RESULT
                }
            }
            steps {
                updateAndRestartService(Environment.TEST, Service.ADMIN_V2)
                checkIfServiceIsRunningInTestEnvironment(Service.ADMIN_V2)
            }
        }
    }

    post {
        always {
            junit testResults: '**/target/surefire-reports/TEST-*.xml', skipPublishingChecks: true
        }
    }
}

def createExclusionPattern() {
    def pom = readMavenPom file: 'pom.xml'

    def exclusions = []
    def plugin = pom.getBuild().getPlugins().find { p -> 'jacoco-maven-plugin' == p.getArtifactId() }
    if (plugin) {
        def lines = plugin.getConfiguration().toString().split('\n')
        lines.each { line ->
            if (line.contains('<exclude>')) {
                exclusions.add(line.replace('<exclude>', '').replace('</exclude>', '').replace(' ', ''))
            }
        }
    }
    return exclusions.join(', ')
}

