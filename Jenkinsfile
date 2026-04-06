pipeline {
    agent any

    environment {
        IMAGE_NAME = "iamsnaaz/cicd-pipeline-demo"
        TAG = "latest"
    }

    stages {

        stage('Checkout') {
            steps {
                git 'https://github.com/iamsnaaz/poc2.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                sh 'echo "No tests yet"'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t $IMAGE_NAME:$TAG .'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-creds',
                    usernameVariable: 'USER',
                    passwordVariable: 'PASS'
                )]) {
                    sh 'echo $PASS | docker login -u $USER --password-stdin'
                    sh 'docker push $IMAGE_NAME:$TAG'
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                docker stop poc-container || true
                docker rm poc-container || true
                docker run -d -p 8080:8080 --name poc-container $IMAGE_NAME:$TAG
                '''
            }
        }
    }
}
