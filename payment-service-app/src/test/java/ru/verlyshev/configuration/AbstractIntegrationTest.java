package ru.verlyshev.configuration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.verlyshev.PaymentServiceApplication;

import java.time.Duration;

@SpringBootTest(classes = PaymentServiceApplication.class)
@Testcontainers
public abstract class AbstractIntegrationTest {
    private static final String PAYMENT_DB_NAME = "payment-db";

    private static final String TEST_USER = "test_user";
    private static final String TEST_PASSWORD = "test_password";

    private static final String DATABASE_ACCEPT_LOG = ".*database system is ready to accept connections.*";
    public static final int LOG_TIMES = 2;
    public static final int LOG_TIMEOUT = 20;

    private static final String SPRING_DATASOURCE_URL = "spring.datasource.url";
    private static final String SPRING_DATASOURCE_USERNAME = "spring.datasource.username";
    private static final String SPRING_DATASOURCE_PASSWORD = "spring.datasource.password";
    private static final String SPRING_KAFKA_BOOTSTRAP_SERVERS = "spring.kafka.bootstrap-servers";

    private static final String POSTGRES_VERSION = "postgres:16";
    private static final String KAFKA_VERSION = "confluentinc/cp-kafka:7.5.0";

    @Container
    protected static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_VERSION)
            .withDatabaseName(PAYMENT_DB_NAME)
            .withUsername(TEST_USER)
            .withPassword(TEST_PASSWORD)
            .waitingFor(new WaitAllStrategy()
                    .withStrategy(Wait.forListeningPort())
                    .withStrategy(Wait.forLogMessage(DATABASE_ACCEPT_LOG, LOG_TIMES)))
            .withStartupTimeout(Duration.ofSeconds(LOG_TIMEOUT));

    @Container
    protected static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse(KAFKA_VERSION)
    );

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add(SPRING_DATASOURCE_URL, POSTGRES::getJdbcUrl);
        registry.add(SPRING_DATASOURCE_USERNAME, POSTGRES::getUsername);
        registry.add(SPRING_DATASOURCE_PASSWORD, POSTGRES::getPassword);

        registry.add(SPRING_KAFKA_BOOTSTRAP_SERVERS, KAFKA::getBootstrapServers);
    }
}
