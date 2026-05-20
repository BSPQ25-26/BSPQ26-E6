package com.example.football_manager;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:football_manager_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.sql.init.mode=never"
})
class FootballManagerApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(FootballManagerApplicationTests.class);

    @Test
    void contextLoads() {
        logger.info("Spring context load test");
        // Verifies that the Spring application context can start using an isolated test database.
    }
}