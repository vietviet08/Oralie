//single build docker image steps

pipeline {

    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-account')
        DOCKERHUB_REPO = 'vietquoc2408'
        LATEST_VERSION = '1.0.4'
        NEXT_VERSION = '1.0.5'
    }

    stages {
//         stage('Checkout') {
//             steps {
//                 git branch: 'master', credentialsId: 'github-account', url: 'https://github.com/vietviet08/Oralie.git'
//             }
//         }
        stage('Build Services') {
            steps {
                script {
                    // First clean up Docker repository
                    // sh 'docker system prune -af'

                    // Build Config Server
                    // dir('configserver') {
                    //     sh """
                    //         if docker images | grep '${DOCKERHUB_REPO}/configserver-oralie'; then
                    //             docker rmi -f ${DOCKERHUB_REPO}/configserver-oralie:${LATEST_VERSION}
                    //         fi
                    //         docker build -t ${DOCKERHUB_REPO}/configserver-oralie:${NEXT_VERSION} .
                    //     """
                    // }

                    // // Build Eureka Server
                    // dir('eurekaserver') {
                    //         sh """
                    //             if docker images | grep '${DOCKERHUB_REPO}/eurekaserver-oralie'; then
                    //                 docker rmi -f ${DOCKERHUB_REPO}/eurekaserver-oralie:${LATEST_VERSION}
                    //             fi
                    //             docker build -t ${DOCKERHUB_REPO}/eurekaserver-oralie:${NEXT_VERSION} .
                    //         """
                    // }

                    // // Build Gateway Server
                    // dir('gatewayserver') {
                    //     sh """
                    //         if docker images | grep '${DOCKERHUB_REPO}/gatewayserver-oralie'; then
                    //             docker rmi -f ${DOCKERHUB_REPO}/gatewayserver-oralie:${LATEST_VERSION}
                    //         fi
                    //         docker build -t ${DOCKERHUB_REPO}/gatewayserver-oralie:${NEXT_VERSION} .
                    //     """
                    // }

                    // // Build Accounts Service
                    // dir('accounts') {
                    //     sh """
                    //         if docker images | grep '${DOCKERHUB_REPO}/accounts-oralie'; then
                    //             docker rmi -f ${DOCKERHUB_REPO}/accounts-oralie:${LATEST_VERSION}
                    //         fi
                    //         docker build -t ${DOCKERHUB_REPO}/accounts-oralie:${NEXT_VERSION} .
                    //     """
                    // }
                    
                    // // Build Product Service
                    // dir('products') {
                    //     sh """
                    //         if docker images | grep '${DOCKERHUB_REPO}/products-oralie'; then
                    //             docker rmi -f ${DOCKERHUB_REPO}/products-oralie:${LATEST_VERSION}
                    //         fi
                    //         docker build -t ${DOCKERHUB_REPO}/products-oralie:${NEXT_VERSION} .
                    //     """
                    // }

                    // // Build Cart Service
                    // dir('carts') {
                    //     sh """
                    //         if docker images | grep '${DOCKERHUB_REPO}/carts-oralie'; then
                    //             docker rmi -f ${DOCKERHUB_REPO}/carts-oralie:${LATEST_VERSION}
                    //         fi
                    //         docker build -t ${DOCKERHUB_REPO}/carts-oralie:${NEXT_VERSION} .
                    //     """
                    // }

                    // // Build Order Service
                    // dir('orders') {
                    //     sh """
                    //         if docker images | grep '${DOCKERHUB_REPO}/orders-oralie'; then
                    //             docker rmi -f ${DOCKERHUB_REPO}/orders-oralie:${LATEST_VERSION}
                    //         fi
                    //         docker build -t ${DOCKERHUB_REPO}/orders-oralie:${NEXT_VERSION} .
                    //     """
                    // }

                    // // Build Notification Service
                    // dir('notification') {
                    //     sh """
                    //         if docker images | grep '${DOCKERHUB_REPO}/notification-oralie'; then
                    //             docker rmi -f ${DOCKERHUB_REPO}/notification-oralie:${LATEST_VERSION}
                    //         fi
                    //         docker build -t ${DOCKERHUB_REPO}/notification-oralie:${NEXT_VERSION} .
                    //     """
                    // }

                    // // Build Social Service
                    // dir('social') {
                    //     sh """
                    //         if docker images | grep '${DOCKERHUB_REPO}/social-oralie'; then
                    //             docker rmi -f ${DOCKERHUB_REPO}/social-oralie:${LATEST_VERSION}
                    //         fi
                    //         docker build -t ${DOCKERHUB_REPO}/social-oralie:${NEXT_VERSION} .
                    //     """
                    // }

                    // Build Search Service
//                     dir('search') {
//                         sh """
//                             if docker images | grep '${DOCKERHUB_REPO}/search-oralie'; then
//                                 docker rmi -f ${DOCKERHUB_REPO}/search-oralie:${LATEST_VERSION}
//                             fi
//                             docker build -t ${DOCKERHUB_REPO}/search-oralie:${NEXT_VERSION} .
//                         """
//                     }

                    // Build Rate Service
                    dir('rates') {
                        sh """
                            if docker images | grep '${DOCKERHUB_REPO}/rates-oralie'; then
                                docker rmi -f ${DOCKERHUB_REPO}/rates-oralie:${LATEST_VERSION}
                            fi
                            docker build -t ${DOCKERHUB_REPO}/rates-oralie:${NEXT_VERSION} .
                        """
                    }

                    // Build Inventory Service
                    dir('inventory') {
                        sh """
                            if docker images | grep '${DOCKERHUB_REPO}/inventory-oralie'; then
                                docker rmi -f ${DOCKERHUB_REPO}/inventory-oralie:${LATEST_VERSION}
                            fi
                            docker build -t ${DOCKERHUB_REPO}/inventory-oralie:${NEXT_VERSION} .
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

                    sh "docker push ${DOCKERHUB_REPO}/carts-oralie:${NEXT_VERSION}"

                    sh "docker push ${DOCKERHUB_REPO}/orders-oralie:${NEXT_VERSION}"

                    sh "docker push ${DOCKERHUB_REPO}/notification-oralie:${NEXT_VERSION}"

                    // sh "docker push ${DOCKERHUB_REPO}/social-oralie:${NEXT_VERSION}"

//                     sh "docker push ${DOCKERHUB_REPO}/search-oralie:${NEXT_VERSION}"

                    sh "docker push ${DOCKERHUB_REPO}/rates-oralie:${NEXT_VERSION}"

                    sh "docker push ${DOCKERHUB_REPO}/inventory-oralie:${NEXT_VERSION}"

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
