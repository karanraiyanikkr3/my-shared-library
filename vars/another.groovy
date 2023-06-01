def anotherMethod(String DIR,String PORTAL) {
    env.AWS_DEFAULT_REGION = "us-east-1"
//    env.IMAGE_REPO_NAME = ${PORTAL}
    env.IMAGE_TAG = "latest"
    env.AWS_ACCOUNT_ID = "885753452070"
    env.REPOSITORY_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_DEFAULT_REGION}.amazonaws.com/${PORTAL}portal"

    try {
        // Logging into AWS ECR
            sh "aws ecr get-login-password --region us-east-1 | sudo docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com"
        
        // Building Docker image
            //sh "sudo docker build -t ${PORTAL}:${env.IMAGE_TAG} $DIR"
        sh "sudo docker build -t ${REPOSITORY_URI}:${env.IMAGE_TAG} ${DIR}"
             // sh "sudo docker build -t ${PORTAL} $DIR"
//                    sh "sudo docker build -t ${REPOSITORY_URI}:${env.IMAGE_TAG} $DIR"
        sh "echo ${env.REPOSITORY_URI}"
        //    docker tag masterportal:latest 885753452070.dkr.ecr.us-east-1.amazonaws.com/masterportal:latest
              sh "sudo docker tag ${PORTAL}:${env.IMAGE_TAG}  :${env.IMAGE_TAG}"
              sh "sudo docker images"
        

        // Pushing to ECR
//            sh "sudo docker push ${PORTAL}:${env.IMAGE_TAG}"
            sh "sudo docker push ${REPOSITORY_URI}:${env.IMAGE_TAG}"
        }
     catch (Exception e) {
        echo "Failed to build and push Docker image: ${e.getMessage()}"
        error "Building and pushing Docker image failed"
    }
}
