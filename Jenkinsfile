pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-account')
        DOCKERHUB_REPO = 'vietquoc2408'
    }

    stages {
//         stage('Checkout') {
//             steps {
//                 git branch: 'master', credentialsId: 'github-account', url: 'https://github.com/vietviet08/Oralie.git'
//             }
//         }

        stage('Build Services') {
            parallel {
                stage('Build ConfigServer') {
                    steps {
                        script {
                            dir('configserver') {
                                sh "docker build -t ${DOCKERHUB_REPO}/configserver-oralie:latest ."
                            }
                        }
                    }
                }

                stage('Build Eureka Server') {
                    steps {
                        script {
                            dir('eurekaserver') {
                                sh "docker build -t ${DOCKERHUB_REPO}/eurekaserver-oralie:latest ."
                            }
                        }
                    }
                }

                stage('Build Gateway Service') {
                    steps {
                        script {
                            dir('gatewayserver') {
                                sh "docker build -t ${DOCKERHUB_REPO}/gatewayserver-oralie:latest ."
                            }
                        }
                    }
                }

                stage('Build Accounts Service') {
                    steps {
                        script {
                            dir('accounts') {
                                sh "docker build -t ${DOCKERHUB_REPO}/accounts-oralie:latest ."
                            }
                        }
                    }
                }
                stage('Build Product Service') {
                    steps {
                        script {
                            dir('products') {
                                sh "docker build -t ${DOCKERHUB_REPO}/products-oralie:latest ."
                            }
                        }
                    }
                }

                stage('Build Cart Service') {
                    steps {
                        script {
                            dir('carts') {
                                sh "docker build -t ${DOCKERHUB_REPO}/carts-oralie:latest ."
                            }
                        }
                    }
                }

                stage('Build Order Service') {
                    steps {
                        script {
                            dir('orders') {
                                sh "docker build -t ${DOCKERHUB_REPO}/orders-oralie:latest ."
                            }
                        }
                    }
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
//                     sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-account', usernameVariable: 'DOCKERHUB_CREDENTIALS_USR', passwordVariable: 'DOCKERHUB_CREDENTIALS_PSW')]) {
                         sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"
                    }
                    sh "docker push ${DOCKERHUB_REPO}/configserver-oralie:latest"

                    sh "docker push ${DOCKERHUB_REPO}/eurekaserver-oralie:latest"

                    sh "docker push ${DOCKERHUB_REPO}/gatewayserver-oralie:latest"

                    sh "docker push ${DOCKERHUB_REPO}/accounts-oralie:latest"

                    sh "docker push ${DOCKERHUB_REPO}/products-oralie:latest"

                    sh "docker push ${DOCKERHUB_REPO}/carts-oralie:latest"

                    sh "docker push ${DOCKERHUB_REPO}/orders-oralie:latest"

                    sh 'docker logout'
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying services...'
// 		        sshagent(['your-server-credentials-id']) {
//                     sh """
//                         ssh -o StrictHostKeyChecking=no user@your-server-ip << EOF
//                         cd /path/to/your/docker-compose/project
//                         docker-compose pull   # Pull the latest images
//                         docker-compose down   # Stop and remove existing containers
//                         docker-compose up -d  # Start the services with the new images
//                     EOF
//                     """
//                 }
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

