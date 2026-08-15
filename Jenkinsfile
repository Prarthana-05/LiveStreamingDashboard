pipeline {

    agent any

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                url: 'https://github.com/Prarthana-05/LiveStreamingDashboard'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar'
            }
        }

       stage('Deploy to Tomcat') {
    steps {
        bat '''
        copy /Y "C:\\ProgramData\\Jenkins\\.jenkins\\workspace\\LiveStreamingDashboard\\target\\LiveStreamingDashboard-0.0.1-SNAPSHOT.war" "C:\\Users\\alpha\\Downloads\\apache-tomcat-10.1.57-windows-x64\\apache-tomcat-10.1.57\\webapps\\LiveStreamingDashboard-0.0.1-SNAPSHOT.war"
        '''
    }
}
    }
}
