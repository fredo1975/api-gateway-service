pipeline {
	environment {
		PROD_SERVER_IP = "192.168.1.106"
		DEV_SERVER_IP = "192.168.1.105"
		GIT_COMMIT_SHORT = sh(
                script: "printf \$(git rev-parse --short HEAD)",
                returnStdout: true
        )
        def VERSION = getArtifactVersion(GIT_COMMIT_SHORT)
        def ARTIFACT = "api-gateway-service-${VERSION}.jar"
	}
    //agent { label 'slave01' }
	agent any
    stages{
		stage ('Initialize') {
            steps {
                sh '''
                    echo "PROD_SERVER_IP = ${PROD_SERVER_IP}"
                    echo "DEV_SERVER_IP = ${DEV_SERVER_IP}"
                    echo "GIT_COMMIT_SHORT = ${GIT_COMMIT_SHORT}"
					echo "VERSION = ${VERSION}"
					echo "ARTIFACT = ${ARTIFACT}"
                '''
            }
        }
        stage('Clone repository') {
			steps {
				script {
					/* Let's make sure we have the repository cloned to our workspace */
					checkout scm
				}
			}
		}
		stage('Build for development') {
			when {
                branch 'develop'
            }
			steps {
				script {
					withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		      			  sh '''
					     	mvn -B org.codehaus.mojo:versions-maven-plugin:2.8.1:set -DprocessAllModules -DnewVersion=${VERSION}
					        mvn -U clean install
					     '''
					}
				}
			}
		}
		stage('Stopping dev api gateway server') {
			when {
                branch 'develop'
            }
	   		steps {
		      	script {
		      		withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		      			sh "ssh jenkins@$DEV_SERVER_IP sudo systemctl stop dvdtheque-api-gateway-server.service"
		      		}
		      	}
		   }
	   }
	   stage('Stopping production api gateway server') {
	   	when {
                branch 'master'
            }
	   		steps {
		      	script {
		      		withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		      			sh "ssh jenkins@$PROD_SERVER_IP sudo systemctl stop dvdtheque-api-gateway-server.service"
		      		}
		      	}
		   }
	   }
	   stage('Copying dev api gateway jar') {
	   		when {
                branch 'develop'
            }
	   		steps {
		      	script {
		      		withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		      			sh "scp target/$ARTIFACT jenkins@$DEV_SERVER_IP:/opt/dvdtheque_api_gateway_server_service/api-gateway-service.jar"
		      		}
		      	}
		    }
	   }
	   stage('Copying production discovery server jar') {
	   		when {
                branch 'master'
            }
	   		steps {
		      	script {
		      		withMaven(mavenSettingsConfig: 'MyMavenSettings') {
				        sh "scp target/$ARTIFACT jenkins@$PROD_SERVER_IP:/opt/dvdtheque_api_gateway_server_service/api-gateway-service.jar"
		      		}
		      	}
		    }
	   }
	   stage('Sarting dev api gateway server') {
	   		when {
                branch 'develop'
            }
	   		steps {
		      	script {
		      		withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		      			sh "ssh jenkins@$DEV_SERVER_IP sudo systemctl start dvdtheque-api-gateway-server.service"
		      		}
		      	}
		    }
	   }
	   stage('Sarting production api gateway server') {
	   		when {
                branch 'master'
            }
	   		steps {
		      	script {
		      		withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		      			sh "ssh jenkins@$PROD_SERVER_IP sudo systemctl start dvdtheque-api-gateway-server.service"
		      		}
		      	}
		    }
	   }
	   stage('Check dev status api gateway server') {
	   		when {
                branch 'develop'
            }
	   		steps {
		      	script {
		      		withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		      			sh "ssh jenkins@$DEV_SERVER_IP sudo systemctl status dvdtheque-api-gateway-server.service"
		      		}
		      	}
		    }
	   }
	   stage('Check productrion status discovery server') {
	   		when {
                branch 'master'
            }
	   		steps {
		      	script {
		      		withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		      			sh "ssh jenkins@$PROD_SERVER_IP sudo systemctl status dvdtheque-api-gateway-server.service"
		      		}
		      	}
		    }
	   }
    }
}

private String getArtifactVersion(String gitRevision){
	def gitBranchName
	gitBranchName = env.GIT_BRANCH
	if(gitBranchName == "develop"){
		return "${gitRevision}-SNAPSHOT"
	}
	if(gitBranchName == "master"){
		gitTagName = sh script: "git describe --tags --all ${gitRevision}", returnStdout: true
		return "${gitRevision}"
	}
	return ""
}