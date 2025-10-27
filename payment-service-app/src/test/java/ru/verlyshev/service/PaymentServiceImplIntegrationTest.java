package ru.verlyshev.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ru.verlyshev.configuration.AbstractIntegrationTest;
import ru.verlyshev.exception.EntityNotFoundException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentServiceImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PaymentServiceImpl paymentService;

    @Test
    @Sql(scripts = "/data/sql/payments-test-data.sql")
    void testGetById() {
        var guid = "a668f828-c2c5-4b83-8c41-ddd8b3ac3781";

        var result = paymentService.getPaymentById(UUID.fromString(guid));

        assertThat(result.guid()).isEqualTo(UUID.fromString(guid));
    }

    @Test
    void testGetByIdNotFound() {
        var guid = "a668f828-c2c5-4b83-8c41-ddd8b3ac3781";

        assertThrows(EntityNotFoundException.class, () -> paymentService.getPaymentById(UUID.fromString(guid)));
    }
}