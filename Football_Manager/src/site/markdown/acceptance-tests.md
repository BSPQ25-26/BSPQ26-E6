# Acceptance Tests

This section documents the acceptance testing strategy used in the Football Manager project.

Acceptance tests validate complete user-oriented scenarios instead of isolated methods. They focus on checking that the application behaves correctly from the user's point of view.

## Implemented acceptance scenarios

The project includes acceptance tests for the Fantasy feature:

- Unauthenticated users are redirected to the login page when trying to access Fantasy.
- Logged users can access the Fantasy dashboard.
- Logged users can create a Fantasy league.
- Logged users can join a Fantasy league using an invite code.
- Logged users receive an error message when using an invalid invite code.
- Logged users can open a specific Fantasy league.
- Logged users can save a Fantasy lineup.

## Technical implementation

The acceptance tests are implemented with:

- Spring Boot Test
- MockMvc
- H2 in-memory database
- Mocked service layer where needed

The main acceptance test class is:

```txt
src/test/java/com/example/football_manager/acceptance/FantasyAcceptanceTest.java
```

## Test results

The execution results are included in the Maven Surefire report:

[Open Surefire Test Report](surefire.html)
