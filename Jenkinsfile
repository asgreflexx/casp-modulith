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
        lines.each{ line ->
            if (line.contains('<exclude>')) {
                exclusions.add(line.replace('<exclude>', '').replace('</exclude>', '').replace(' ', ''))
            }
        }
    }
    return exclusions.join(', ')
}
