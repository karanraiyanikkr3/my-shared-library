pipeline {
    agent any
    
    environment {
        // Define your environment variables
        AWS_DEFAULT_REGION = 'us-east-1'
        IMAGE_REPO_NAME = 'my-image-repo'
        IMAGE_TAG = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        AWS_ACCOUNT_ID = credentials('aws-account-id')
        REPOSITORY_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}"
    }
    
    stages {
        stage('Build Docker Image') {
            steps {
                // Build your Docker image
                sh "docker build -t ${IMAGE_REPO_NAME}:${IMAGE_TAG} ."
            }
        }
        
        stage('Push Docker Image to ECR') {
            steps {
                // Login to ECR
                withCredentials([string(credentialsId: 'aws-ecr-credentials', variable: 'AWS_ECR_CREDENTIALS')]) {
                    sh "docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com <<< ${AWS_ECR_CREDENTIALS}"
                }
                 
                // Tag the image with latest and version
                sh "docker tag ${IMAGE_REPO_NAME}:${IMAGE_TAG} ${REPOSITORY_URI}:latest"
                sh "docker tag ${IMAGE_REPO_NAME}:${IMAGE_TAG} ${REPOSITORY_URI}:${IMAGE_TAG}"

                
                // Push the image to ECR
                sh "docker push ${REPOSITORY_URI}:latest"
                sh "docker push ${REPOSITORY_URI}:${IMAGE_TAG}"
            }
        }
    }
    
    post {
        always {
            // Cleanup Docker images
            sh "docker rmi ${IMAGE_REPO_NAME}:${IMAGE_TAG} || true"
            sh "docker rmi ${REPOSITORY_URI}:latest || true"
            sh "docker rmi ${REPOSITORY_URI}:${IMAGE_TAG} || true"
        }
    }
}
