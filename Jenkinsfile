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
                 bat 'mvn test -Dtest=LiveStreamingDashboardApplicationTests'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.war'
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                bat '''
                copy /Y "C:\\ProgramData\\Jenkins\\.jenkins\\workspace\\LiveStreamingDashboard\\target\\LiveStreamingDashboard-0.0.1-SNAPSHOT.war" "C:\\Users\\alpha\\Downloads\\apache-tomcat-10.1.57-windows-x64\\apache-tomcat-10.1.57\\webapps\\LiveStreamingDashboard-0.0.1-SNAPSHOT.war"
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                bat '''
                copy /Y "target\\LiveStreamingDashboard-0.0.1-SNAPSHOT.war" "C:\\Users\\alpha\\Downloads\\LiveStreamingDocker\\LiveStreamingDashboard-0.0.1-SNAPSHOT.war"

                cd /d "C:\\Users\\alpha\\Downloads\\LiveStreamingDocker"

                docker build -t livestream-dashboard:1.0 .
                '''
            }
        }

        stage('Deploy Docker Container') {
            steps {
                bat '''
                docker stop livestream-dashboard-container || exit /b 0
                docker rm livestream-dashboard-container || exit /b 0

                docker run -d --name livestream-dashboard-container -p 8083:8082 livestream-dashboard:1.0
                '''
            }
        }
    }
}