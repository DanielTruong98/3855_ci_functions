def call(dockerRepoName, imageName, portNum) {
  pipeline {
    agent any
    stages {
      stage('Build') {
        steps {
          sh "pip install -r ${dockerRepoName}/requirements.txt"
        }
      }
      stage('Python Lint') {
        steps {
          sh "pylint-fail-under --fail_under 5 ${dockerRepoName}/app.py"
        }
      }
      stage('Package') {
        steps {
          withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
            sh "docker login -u 'dtruong98' -p '$TOKEN' docker.io"
            sh "docker build -t ${dockerRepoName}:latest --tag dtruong98/${dockerRepoName}:${imageName} ${dockerRepoName}"
            sh "docker push dtruong98/${dockerRepoName}:${imageName}"
          }
        }
      }
      stage('Zip Artifacts') {
        steps {
          sh "zip -r app.zip ${dockerRepoName}"
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
