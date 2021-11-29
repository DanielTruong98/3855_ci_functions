def call(dockerRepoName, imageName, portNum) {
  pipeline {
    agent any
    stages {
      stage('Build') {
        steps {
          sh 'pip install -r requirements.txt'
        }
      }
      stage('Python Lint') {
        steps {
          sh 'pylint-fail-under --fail-under 5 *.py'
        }
      }
      stage('Package') {
        steps {
          withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
            sh "docker login -u 'dtruong98' -p '$TOKEN' docker.io"
            sh "docker build -t ${dockerRepoName}:latest --tag dtruong98/${dockerRepoName}:${imageName} ."
            sh "docker push dtruong98/${dockerRepoName}:${imageName}"
          }
        }
      }
      stage('Zip Artifacts') {
        steps {
          sh "zip app.zip *.py"
        }
        post {
          always {
            archiveArtifacts 'app.zip'
          }
        }
      }
    }
  }
}
