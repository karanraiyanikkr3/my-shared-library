def call(String DIR) {
    env.AWS_DEFAULT_REGION = "us-east-1"
    env.IMAGE_REPO_NAME = "masterportal"
    env.IMAGE_TAG = "latest"
    env.AWS_ACCOUNT_ID = "885753452070"
    def REPOSITORY_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_DEFAULT_REGION}.amazonaws.com/${env.IMAGE_REPO_NAME}"

    try {
        // Logging into AWS ECR
            sh "aws ecr get-login-password --region us-east-1 | sudo docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com"
        
        // Building Docker image
            sh "sudo docker build -t ${REPOSITORY_URI}:${env.IMAGE_TAG} $DIR"
            sh "sudo docker images"
        

        // Pushing to ECR
            sh "sudo docker push ${REPOSITORY_URI}:${env.IMAGE_TAG}"
        }
     catch (Exception e) {
        echo "Failed to build and push Docker image: ${e.getMessage()}"
        error "Building and pushing Docker image failed"
    }
}
