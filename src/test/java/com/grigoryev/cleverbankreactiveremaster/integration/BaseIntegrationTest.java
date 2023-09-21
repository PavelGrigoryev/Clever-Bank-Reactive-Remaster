package com.grigoryev.cleverbankreactiveremaster.integration;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public abstract class BaseIntegrationTest {

    private static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>("postgres:15.4");

    @BeforeAll
    static void starContainer() {
        CONTAINER.start();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                String.format("r2dbc:postgresql://%s:%s@%s:%d/%s",
                        CONTAINER.getUsername(),
                        CONTAINER.getPassword(),
                        CONTAINER.getHost(),
                        CONTAINER.getFirstMappedPort(),
                        CONTAINER.getDatabaseName()
                )
        );
    }

}
