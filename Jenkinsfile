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
                jacoco  changeBuildStatus: true,
                        exclusionPattern: '**/data/access/layer/documents/**/*.class, **/*Test*.class, **/*AdminApplication.class, **/data/access/layer/repositories/*.class, **/presentation/layer/configuration/*.class, **/presentation/layer/dtos/**/*.class',
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
    }

    post {
        always {
            junit testResults: '**/target/surefire-reports/TEST-*.xml', skipPublishingChecks: true
        }
    }
}
