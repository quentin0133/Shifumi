pipeline {
    agent any

    tools {
        maven "Maven 3.9.6"
    }

    stages {
        stage('Hello') {
            steps {
                script {
                    echo 'Hello world!'
                }
            }
        }

        stage('Maven Build') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    dockerImage = docker.build('shifumeunmi:lastest')
                }
            }
        }

        stage('Start Docker Container') {
            steps {
                bat "docker run --name shifumeunmi -d -p 33470:8080 shifumeunmi:lastest ShiFumeUnMi.jar"
            }
        }
    }
}