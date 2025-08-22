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
                recordCoverage(tools: [[parser: 'JACOCO']])
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
            }
        }
    }

    post {
        always {
            junit testResults: '**/target/surefire-reports/TEST-*.xml', skipPublishingChecks: true
        }
    }
}
