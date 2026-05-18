# Football Manager

Football Manager is a Spring Boot web application developed for managing football teams, matches, results, users and favourite teams.

The project was developed as part of the Software Process and Quality course, including automated testing, performance testing, continuous integration and generated documentation.

---

## Main Features

- User registration and login.
- Admin and regular user roles.
- Team management.
- Country management.
- Competition management.
- Match management.
- Match results.
- Favourite teams.
- Web views using Thymeleaf.
- REST API endpoints.
- Automated unit and integration tests.
- Code coverage with JaCoCo.
- Performance testing with JUnitPerf.
- Continuous Integration with Jenkins.
- Documentation generated with Doxygen.
- Maven Site generation.

---

## Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- Thymeleaf
- PostgreSQL
- Maven
- JUnit 5
- JUnit 4
- Mockito
- JUnitPerf
- JaCoCo
- Jenkins
- Doxygen
- Maven Site
- Docker

---

## Project Structure

```text
BSPQ26-E6/
│
├── Football_Manager/
│   ├── src/main/java/com/example/football_manager/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   │
│   ├── src/main/resources/
│   │   ├── templates/
│   │   ├── static/
│   │   └── application.properties
│   │
│   ├── src/test/java/com/example/football_manager/
│   │   ├── controller/
│   │   ├── performance/
│   │   └── service/
│   │
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── docs/
│   ├── doxygen/
│   └── sprint3/
│
├── Doxyfile
├── Jenkinsfile
└── README.md
```

---

## Requirements

Before running the project, make sure you have:

- Java 17 installed.
- PostgreSQL installed and running.
- Git installed.
- Maven Wrapper included in the project.
- Docker installed if you want to run Jenkins or documentation tools through Docker.

Check Java version:

```bash
java -version
```

Expected version:

```text
Java 17
```

---

## Database Configuration

The application uses PostgreSQL.

Check the database configuration in:

```text
Football_Manager/src/main/resources/application.properties
```

Typical configuration example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/football_manager
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

Make sure the database exists before running the application.

---

## How to Run the Application

From the project folder:

### Windows

```powershell
cd Football_Manager
.\mvnw.cmd spring-boot:run
```

### Linux / macOS

```bash
cd Football_Manager
chmod +x mvnw
./mvnw spring-boot:run
```

The application will be available at:

```text
http://localhost:8080
```

---

## How to Run Tests

### Windows

```powershell
cd Football_Manager
.\mvnw.cmd test
```

### Linux / macOS

```bash
cd Football_Manager
./mvnw test
```

---

## Code Coverage with JaCoCo

To run tests and generate the JaCoCo coverage report:

### Windows

```powershell
cd Football_Manager
.\mvnw.cmd clean test jacoco:report
```

### Linux / macOS

```bash
cd Football_Manager
./mvnw clean test jacoco:report
```

The report is generated in:

```text
Football_Manager/target/site/jacoco/index.html
```

---

## Performance Testing with JUnitPerf

The project uses JUnitPerf for performance testing.

Performance tests are located in:

```text
Football_Manager/src/test/java/com/example/football_manager/performance/
```

To run only the performance tests:

### Windows

```powershell
cd Football_Manager
.\mvnw.cmd test -Dtest=TeamServicePerformanceTest
```

### Linux / macOS

```bash
cd Football_Manager
./mvnw test -Dtest=TeamServicePerformanceTest
```

JUnitPerf generates an HTML report in:

```text
Football_Manager/build/reports/junitperf_report.html
```

The performance tests include:

- Single-thread execution.
- Multi-thread execution.
- Throughput validation.
- Duration-based execution.
- Mean latency validation.
- Maximum latency validation.
- An intentionally failing test for Sprint 2 evidence.

---

## Jenkins CI Pipeline

The repository includes a `Jenkinsfile` for Sprint 3 Continuous Integration.

The Jenkins pipeline performs:

1. Checkout from GitHub.
2. Java version verification.
3. Maven compile.
4. Test execution.
5. JaCoCo report generation.
6. Package generation.
7. Artifact archiving.

The Jenkinsfile is located at:

```text
Jenkinsfile
```

To test the Jenkins pipeline:

1. Open Jenkins.
2. Create a new Pipeline job.
3. Select `Pipeline script from SCM`.
4. Select Git.
5. Use the repository URL:

```text
https://github.com/BSPQ25-26/BSPQ26-E6.git
```

6. Use branch:

```text
*/main
```

7. Use script path:

```text
Jenkinsfile
```

8. Run `Build Now`.

A successful execution should finish with:

```text
Finished: SUCCESS
```

---

## Doxygen Documentation

The project includes a Doxygen configuration file:

```text
Doxyfile
```

Doxygen generates documentation from the Java source code.

To generate Doxygen documentation using Docker:

```powershell
docker run --rm -v "${PWD}:/project" -w /project alpine:latest sh -c "apk add --no-cache doxygen && doxygen Doxyfile"
```

Generated HTML documentation:

```text
docs/doxygen/html/index.html
```

Generated LaTeX documentation:

```text
docs/doxygen/latex/
```

Generated PDF documentation, if compiled:

```text
docs/doxygen/latex/refman.pdf
```

---

## Swagger/OpenAPI Documentation

The project includes Swagger/OpenAPI documentation for the REST API.

The Swagger documentation is available at:

```text
/swagger-ui/index.html
```

The OpenAPI specification is available at:

```text
/v3/api-docs
```

---

## Maven Site

To generate the Maven Site:

### Windows

```powershell
cd Football_Manager
.\mvnw.cmd site
```

### Linux / macOS

```bash
cd Football_Manager
./mvnw site
```

The generated site is located at:

```text
Football_Manager/target/site/index.html
```

The Maven Site includes project information and generated reports.

---

## Main API Endpoints

Some of the main REST endpoints are:

```text
GET    /api/teams
POST   /api/teams
PUT    /api/teams/{id}
DELETE /api/teams/{id}

GET    /api/countries
POST   /api/countries
DELETE /api/countries/{id}

GET    /api/competitions
POST   /api/competitions
PUT    /api/competitions/{id}
DELETE /api/competitions/{id}

GET    /api/matches
POST   /api/matches
PUT    /api/matches/{id}
DELETE /api/matches/{id}

GET    /api/matches/results
PATCH  /api/matches/{id}/result

POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/logout
```

---

## Web Views

Main web pages include:

```text
/login
/register
/teams
/teams/{id}
/matches/schedule
/matches/results
/profile
/admin
/admin/manage-teams
```

---

## Testing Strategy

The project includes different types of tests:

- Unit tests for service classes.
- Controller tests.
- Authentication tests.
- Performance tests.
- Coverage reports with JaCoCo.

Main tested areas:

- User authentication.
- Country management.
- Competition management.
- Match management.
- Team management.
- Performance of team retrieval operations.

---

## Sprint 2 Evidence

Sprint 2 included:

- Unit tests.
- Controller tests.
- JaCoCo coverage report.
- Performance tests using JUnitPerf.
- Successful performance execution.
- Intentionally failing performance execution.
- Performance HTML report.
- VisualVM evidence, if available.
- Release 2 evidence.

Relevant generated files:

```text
Football_Manager/target/site/jacoco/
Football_Manager/build/reports/junitperf_report.html
docs/sprint2/
```

---

## Sprint 3 Evidence

Sprint 3 includes:

- Jenkins CI pipeline.
- Successful Jenkins build evidence.
- Doxygen HTML documentation.
- Doxygen LaTeX/PDF documentation.
- Maven Site.
- Improved README documentation.
- Release 3 evidence.

Relevant files:

```text
Jenkinsfile
Doxyfile
docs/doxygen/
docs/sprint3/
Football_Manager/target/site/
```

---

## How to Package the Application

To generate the `.jar` file:

### Windows

```powershell
cd Football_Manager
.\mvnw.cmd package -DskipTests
```

### Linux / macOS

```bash
cd Football_Manager
./mvnw package -DskipTests
```

The generated artifact is located in:

```text
Football_Manager/target/
```

---

## Authors

Group E6

Team members:

- Alba Delgado Ortega
- Alejandro Vicente Milla Tinajero
- Alex Ramarathinam Aguirre
- Elena Torralbo Jiménez
- Iker Castillo Peiteado
- Jaime Etxebarria Ugarte
- Javier Gómez Martínez

---

## Repository

```text
https://github.com/BSPQ25-26/BSPQ26-E6
```

---

## Project Status

The project includes the main functionality required for the course project, together with testing, performance evaluation, continuous integration and generated documentation evidence for Sprint 2 and Sprint 3.
