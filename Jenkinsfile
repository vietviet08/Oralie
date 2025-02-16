pipeline {

    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-account')
        DOCKERHUB_REPO = 'vietquoc2408'
        LATEST_VERSION = '1.0.3'
        NEXT_VERSION = '1.0.4'
    }

    stages {
        stage('Clean up Docker repository') {
            steps {
                script {
                    sh 'docker system prune -af'
                }
            }
        }

        stage('Build Config Server') {
            steps {
                script {
                    dir('configserver') {
                        sh """
                            if docker images | grep '${DOCKERHUB_REPO}/configserver-oralie'; then
                                docker rmi -f ${DOCKERHUB_REPO}/configserver-oralie:${LATEST_VERSION}
                            fi
                            docker build -t ${DOCKERHUB_REPO}/configserver-oralie:${NEXT_VERSION} .
                        """
                    }
                }
            }
        }

        stage('Build Eureka Server') {
            steps {
                script {
                    dir('eurekaserver') {
                        sh """
                            if docker images | grep '${DOCKERHUB_REPO}/eurekaserver-oralie'; then
                                docker rmi -f ${DOCKERHUB_REPO}/eurekaserver-oralie:${LATEST_VERSION}
                            fi
                            docker build -t ${DOCKERHUB_REPO}/eurekaserver-oralie:${NEXT_VERSION} .
                        """
                    }
                }
            }
        }

        stage('Build Gateway Server') {
            steps {
                script {
                    dir('gatewayserver') {
                        sh """
                            if docker images | grep '${DOCKERHUB_REPO}/gatewayserver-oralie'; then
                                docker rmi -f ${DOCKERHUB_REPO}/gatewayserver-oralie:${LATEST_VERSION}
                            fi
                            docker build -t ${DOCKERHUB_REPO}/gatewayserver-oralie:${NEXT_VERSION} .
                        """
                    }
                }
            }
        }

        stage('Build Accounts Service') {
            steps {
                script {
                    dir('accounts') {
                        sh """
                            if docker images | grep '${DOCKERHUB_REPO}/accounts-oralie'; then
                                docker rmi -f ${DOCKERHUB_REPO}/accounts-oralie:${LATEST_VERSION}
                            fi
                            docker build -t ${DOCKERHUB_REPO}/accounts-oralie:${NEXT_VERSION} .
                        """
                    }
                }
            }
        }

        stage('Build Product Service') {
            steps {
                script {
                    dir('products') {
                        sh """
                            if docker images | grep '${DOCKERHUB_REPO}/products-oralie'; then
                                docker rmi -f ${DOCKERHUB_REPO}/products-oralie:${LATEST_VERSION}
                            fi
                            docker build -t ${DOCKERHUB_REPO}/products-oralie:${NEXT_VERSION} .
                        """
                    }
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-account', usernameVariable: 'DOCKERHUB_CREDENTIALS_USR', passwordVariable: 'DOCKERHUB_CREDENTIALS_PSW')]) {
                         sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"
                    }
                    sh "docker push ${DOCKERHUB_REPO}/configserver-oralie:${NEXT_VERSION}"
                    sh "docker push ${DOCKERHUB_REPO}/eurekaserver-oralie:${NEXT_VERSION}"
                    sh "docker push ${DOCKERHUB_REPO}/gatewayserver-oralie:${NEXT_VERSION}"
                    sh "docker push ${DOCKERHUB_REPO}/accounts-oralie:${NEXT_VERSION}"
                    sh "docker push ${DOCKERHUB_REPO}/products-oralie:${NEXT_VERSION}"

                    sh 'docker logout'
                }
            }
        }

        stage('Clean up Docker repository after') {
            steps {
                script {
                    sh 'docker system prune -af'
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed.'
        }
        success {
            echo 'Build and push to Docker Hub successful!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}

