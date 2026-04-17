pipeline {
    agent any

    environment {
        IMAGE_NAME = "iamsnaaz/cicd-pipeline-demo"
        TAG = "latest"
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/iamsnaaz/POC1_demo.git'
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

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('OWASP Scan') {
            steps {
                withCredentials([string(credentialsId: 'nvd-api-key', variable: 'NVD_KEY')]) {
                    dependencyCheck(
                        odcInstallation: 'Dependency-Check',
                        additionalArguments: '--scan . --out ./dc-report --format XML --format HTML --noupdate'
                    )
                }

                dependencyCheckPublisher(
                    pattern: 'dc-report/dependency-check-report.xml'
                )
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t $IMAGE_NAME:$TAG .'
            }
        }

       stage('Trivy Scan') {
            steps {
                script {
                    sh '''
                    mkdir -p trivy-cache
        
                    # Step 1: Download DB (ignore failure)
                    trivy image --download-db-only --cache-dir trivy-cache || true
        
                    # Step 2: Run scan (never fail pipeline)
                    trivy image \
                    --cache-dir trivy-cache \
                    --skip-db-update \
                    --scanners vuln \
                    --exit-code 0 \
                    $IMAGE_NAME:$TAG || true
                    '''
                }
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
                docker run -d -p 8081:8080 --name poc-container $IMAGE_NAME:$TAG
                '''
            }
        }
    }

    post {
        always {
            sh '''
            docker system prune -af || true
            rm -rf trivy-cache || true
            '''
        }
    }
}
