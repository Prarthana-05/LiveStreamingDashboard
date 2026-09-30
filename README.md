# Live Streaming Status Dashboard

A Spring Boot web application for tracking, searching, and monitoring the status of live streaming events in real time — built end-to-end with a full DevOps lifecycle: Git/GitHub, Jenkins CI/CD, Selenium automated testing, Docker containerization, and Ansible configuration management.

**Repository:** https://github.com/Prarthana-05/LiveStreamingDashboard

---

## Overview

Stream operators currently have no centralized way to track which streams are active, their current status, or quality issues in real time. This dashboard solves that by letting operators:

- Add new stream/event entries
- View all streams on a searchable dashboard
- Search for a specific stream by name
- Filter streams by status (e.g., online)
- See summary indicators for a quick operational overview

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend Framework | Spring Boot 4.1.0 (Java 21) |
| View Layer | Thymeleaf |
| Data Layer | Spring Data JPA + Hibernate |
| Database | MySQL 8.0 |
| Build Tool | Maven |
| Version Control | Git + GitHub |
| CI/CD | Jenkins (Declarative Pipeline) |
| UI Testing | Selenium WebDriver + JUnit 5 |
| Containerization | Docker |
| Configuration Management | Ansible (run via Docker) |
| Deployment Target | Apache Tomcat 10.1 / Docker container |

---

## Architecture

```
Browser (Thymeleaf UI)
        │
        ▼
DashboardController (Spring MVC)
        │
        ▼
Stream Repository (Spring Data JPA)
        │
        ▼
MySQL (live_stream_dashboard schema)
```

**Deployment paths:**
- **Direct:** WAR deployed to Apache Tomcat 10.1, served at `/LiveStreamingDashboard-0.0.1-SNAPSHOT/`
- **Containerized:** WAR packaged into a Docker image, run as `java -jar app.war`, served at root `/`

---

## Project Structure

```
LiveStreamingDashboard/
├── src/
│   ├── main/
│   │   ├── java/com/prarthana/livestreamingdashboard/   # application source
│   │   └── resources/                                    # templates, static assets, application.properties
│   └── test/
│       └── java/com/prarthana/livestreamingdashboard/    # Selenium + unit tests
├── Jenkinsfile                                            # CI/CD pipeline as code
├── Dockerfile                                             # container image definition
├── pom.xml                                                # Maven build configuration
└── .gitignore
```

---

## Data Model

| Field | Description |
|---|---|
| `streamName` | Name of the stream/event |
| `channelName` | Channel associated with the stream |
| `status` | Current status (e.g., online/offline) |
| `quality` | Stream quality (e.g., HD) |
| `location` | Geographic location of the stream |

## API Endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/` | Dashboard view listing all streams |
| `GET` | `/add` | Add Stream form page |
| `POST` | `/add` | Submit new stream (form-encoded) |
| `GET` | `/search?name=` | Search streams by name |
| `GET` | `/status?value=` | Filter streams by status |

---

## Local Setup

### Prerequisites
- JDK 21
- Apache Maven
- MySQL 8.0 (running as a local service)
- IntelliJ IDEA (recommended) or any Java IDE

### Steps

1. **Clone the repository**
   ```
   git clone https://github.com/Prarthana-05/LiveStreamingDashboard.git
   cd LiveStreamingDashboard
   ```

2. **Start MySQL** and create the schema:
   ```sql
   CREATE DATABASE live_stream_dashboard;
   ```

3. **Configure** `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/live_stream_dashboard
   spring.datasource.username=root
   spring.datasource.password=<your-password>
   server.port=8082
   ```

4. **Run the application:**
   ```
   mvn spring-boot:run
   ```

5. **Open in browser:**
   ```
   http://localhost:8082/
   ```

---

## Running Tests

The project includes 5 Selenium WebDriver test cases covering the core user journeys:

1. Dashboard loads successfully
2. Add Stream page loads
3. Adding a new stream
4. Searching for a stream
5. Filtering by online status

Run locally with:
```
mvn test -Dtest=SeleniumDashboardTest
```

A `TestWatcher` extension automatically captures a screenshot to `target/screenshots/` on any test failure.

---

## CI/CD Pipeline (Jenkins)

The `Jenkinsfile` defines a declarative pipeline with the following stages:

1. **Checkout** — pulls source from GitHub (`main` branch)
2. **Build** — `mvn clean package` produces the WAR artifact
3. **Deploy to Tomcat** — copies the WAR into Tomcat's `webapps/` folder and restarts Tomcat
4. **Test** — runs the Spring context test + full Selenium suite; a failure halts the pipeline
5. **Archive** — archives the built WAR as a Jenkins build artifact
6. **Build Docker Image** — packages the WAR into a versioned Docker image
7. **Deploy Docker Container** — replaces the running container with a fresh one from the new image

A failing test at the **Test** stage blocks all subsequent stages, enforcing a quality gate before deployment.

---

## Docker

**Build the image:**
```
docker build -t livestream-dashboard:1.0 .
```

**Run the container:**
```
docker run -d --name livestream-dashboard-container -p 8083:8082 -e SPRING_PROFILES_ACTIVE=docker livestream-dashboard:1.0
```

**Access:**
```
http://localhost:8083/
```

> The `docker` Spring profile (`application-docker.properties`) overrides the database host to `host.docker.internal`, allowing the containerized app to reach MySQL running on the host machine.

**Container lifecycle:**
```
docker ps                                              # view running containers
docker logs livestream-dashboard-container             # view logs
docker stop livestream-dashboard-container              # stop
docker start livestream-dashboard-container              # restart
docker rm -f livestream-dashboard-container              # remove
```

---

## Configuration Management (Ansible)

An Ansible playbook (`playbook.yml`) validates server prerequisites and performs a health check against the running application. It's executed via a Docker-based Ansible runner (no native Ansible install required on Windows):

```
docker run -d --name ansible-runner -v <path-to-ansible-folder>:/ansible willhallonline/ansible:latest sleep infinity
docker exec ansible-runner ansible-playbook /ansible/playbook.yml
```

The playbook demonstrates:
- **Provisioning** — creates required folders/marker files
- **Idempotency** — a second run reports zero changes
- **Health check** — HTTP check against the running app (PASS/FAIL)
- **Rollback/recovery** — validated by stopping the container (health check FAILs), then restoring from a tagged `stable` image (health check PASSes again)

---

## Troubleshooting

| Issue | Fix |
|---|---|
| Port 8080 conflict (Jenkins vs Spring Boot) | App runs on port 8082 instead |
| `Connection refused` to MySQL | Ensure MySQL service is running: `net start MYSQL80` **before** starting Tomcat/the app |
| Selenium `element click intercepted` | Scroll element into view + explicit wait before clicking |
| Docker container exits immediately | Activate the `docker` Spring profile: `-e SPRING_PROFILES_ACTIVE=docker` |
| Jenkins node offline (low disk space) | Prune unused Docker images (`docker system prune -a --volumes`) and compact the WSL2 virtual disk |
| 404 after Tomcat deploy | Wait for full redeploy (can take 1–3+ minutes); confirm MySQL was running *before* Tomcat started |

---

## Limitations

- Tomcat deployment path is hardcoded to a local file path (not portable across machines)
- Single Jenkins node; no distributed/agent-based builds
- Rollback to a stable Docker image is a manual step, not an automated pipeline trigger
- Windows-specific setup (batch scripts, local paths)
- Docker images are local only; no push to Docker Hub or a remote registry

## Future Enhancements

- Push versioned Docker images to Docker Hub
- Automated rollback triggered by a failing post-deploy health check
- Ansible-based provisioning of a clean remote/VM target
- Jenkins JUnit test report publishing for a graphical pass/fail view
- Externalize credentials and hardcoded paths into environment variables

---


