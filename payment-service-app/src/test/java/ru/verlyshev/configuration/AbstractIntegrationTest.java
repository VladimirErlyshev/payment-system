package ru.verlyshev.configuration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.verlyshev.PaymentServiceApplication;

import java.time.Duration;

@SpringBootTest(classes = PaymentServiceApplication.class)
@Testcontainers
public abstract class AbstractIntegrationTest {
    private static final String PAYMENT_DB_NAME = "payment-db";
    private static final String TEST_USER = "test_user";
    private static final String TEST_PASSWORD = "test_password";
    private static final String DATABASE_ACCEPT_LOG = ".*database system is ready to accept connections.*";
    private static final String SPRING_DATASOURCE_URL = "spring.datasource.url";
    private static final String SPRING_DATASOURCE_USERNAME = "spring.datasource.username";
    private static final String SPRING_DATASOURCE_PASSWORD = "spring.datasource.password";
    private static final String POSTGRES_VERSION = "postgres:16";

    @Container
    protected static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_VERSION)
            .withDatabaseName(PAYMENT_DB_NAME)
            .withUsername(TEST_USER)
            .withPassword(TEST_PASSWORD)
            .waitingFor(new WaitAllStrategy()
                    .withStrategy(Wait.forListeningPort())
                    .withStrategy(Wait.forLogMessage(DATABASE_ACCEPT_LOG, 2)))
            .withStartupTimeout(Duration.ofSeconds(20));

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add(SPRING_DATASOURCE_URL, POSTGRES::getJdbcUrl);
        registry.add(SPRING_DATASOURCE_USERNAME, POSTGRES::getUsername);
        registry.add(SPRING_DATASOURCE_PASSWORD, POSTGRES::getPassword);
    }
}
