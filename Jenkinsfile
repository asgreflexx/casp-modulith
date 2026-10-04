//file:noinspection GroovyAssignabilityCheck
//noinspection GroovyUnusedAssignment
@Library('Common@develop') _
import casp.common.Environment
import casp.common.Service

pipeline {

    agent any

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
        skipStagesAfterUnstable()
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
            when { branch 'develop' }
            steps {
                buildImageAndPush(Service.ADMIN_V2)
            }
        }

        stage('Restart Service') {
            when { branch 'develop' }
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
