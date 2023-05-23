def call(String URL) {
    env.AWS_DEFAULT_REGION = "us-east-1"
    env.IMAGE_REPO_NAME = "jenkinsbuild"
    env.IMAGE_TAG = "latest"

    try {
        // Logging into AWS ECR
        withCredentials([string(credentialsId: 'AWS_ACCOUNT_ID', variable: 'AWS_ACCOUNT_ID')]) {
            sh "aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com"
        }

        // Cloning Git
        //git 'https://github.com/karanraiyanikkr3/node-todo-cicd.git/'
        git url:URL
//        echo '${config.url}'

        // Building Docker image
        sh "docker build -t ${env.IMAGE_REPO_NAME} ."
        sh "docker images"

        // Pushing to ECR
        withCredentials([string(credentialsId: 'AWS_ACCOUNT_ID', variable: 'AWS_ACCOUNT_ID')]) {
            def REPOSITORY_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/jenkinsbuild"
            sh "docker tag ${env.IMAGE_REPO_NAME}:${env.IMAGE_TAG} ${REPOSITORY_URI}:${env.IMAGE_TAG}"
            sh "docker push ${REPOSITORY_URI}:${env.IMAGE_TAG}"
        }
    } catch (Exception e) {
        echo "Failed to build and push Docker image: ${e.getMessage()}"
        error "Building and pushing Docker image failed"
    }
}
