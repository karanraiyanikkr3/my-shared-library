def call(String DIR) {
    env.AWS_DEFAULT_REGION = "us-east-1"
    env.IMAGE_REPO_NAME = "masterportal"
    env.AWS_ACCOUNT_ID = credentials('AWS_ACCOUNT_ID')

    try {
        // Logging into AWS ECR
        withCredentials([string(credentialsId: 'AWS_ACCOUNT_ID', variable: 'AWS_ACCOUNT_ID')]) {
            sh "aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com"
        }

        // Building Docker image
        sh "docker build -t ${env.IMAGE_REPO_NAME} $DIR"
        sh "docker images"

        // Pushing to ECR with version and proper tags
        def IMAGE_TAG = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        def REPOSITORY_URI = "${env.AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_DEFAULT_REGION}.amazonaws.com/${env.IMAGE_REPO_NAME}"
        sh "docker tag ${env.IMAGE_REPO_NAME} ${REPOSITORY_URI}:${IMAGE_TAG}"
        //sh "docker tag ${env.IMAGE_REPO_NAME} ${REPOSITORY_URI}:latest"
        sh "docker push ${REPOSITORY_URI}:${IMAGE_TAG}"
       // sh "docker push ${REPOSITORY_URI}:latest"
    } catch (Exception e) {
        echo "Failed to build and push Docker image: ${e.getMessage()}"
        error "Building and pushing Docker image failed"
    }
}
