pipeline {
    agent any

    environment {
        PROJECT_DIR = 'Football_Manager'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Java') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'java -version'
                    } else {
                        bat 'java -version'
                    }
                }
            }
        }

        stage('Compile') {
            steps {
                dir("${PROJECT_DIR}") {
                    script {
                        if (isUnix()) {
                            sh 'chmod +x mvnw'
                            sh './mvnw -B clean compile'
                        } else {
                            bat 'mvnw.cmd -B clean compile'
                        }
                    }
                }
            }
        }

        stage('Test and Coverage') {
            steps {
                dir("${PROJECT_DIR}") {
                    script {
                        if (isUnix()) {
                            sh './mvnw -B test jacoco:report'
                        } else {
                            bat 'mvnw.cmd -B test jacoco:report'
                        }
                    }
                }
            }
        }

        stage('Package') {
            steps {
                dir("${PROJECT_DIR}") {
                    script {
                        if (isUnix()) {
                            sh './mvnw -B package -DskipTests'
                        } else {
                            bat 'mvnw.cmd -B package -DskipTests'
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'Football_Manager/target/surefire-reports/*.xml'

            archiveArtifacts artifacts: '''
                Football_Manager/target/*.jar,
                Football_Manager/target/site/jacoco/**,
                Football_Manager/build/reports/**,
                Football_Manager/target/surefire-reports/**
            ''', allowEmptyArchive: true
        }

        success {
            echo 'Pipeline completed successfully: build, tests, coverage and package passed.'
        }

        failure {
            echo 'Pipeline failed. Check test results, JaCoCo report or compilation errors.'
        }
    }
}