package ru.verlyshev.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.verlyshev.configuration.AbstractIntegrationTest;
import ru.verlyshev.dto.PaymentFilterDto;
import ru.verlyshev.exception.EntityNotFoundException;
import ru.verlyshev.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.verlyshev.fixtures.TestFixtures.EXISTING_GUID;
import static ru.verlyshev.fixtures.TestFixtures.NOT_EXISTING_GUID;
import static ru.verlyshev.fixtures.TestFixtures.checkPaymentDto;
import static ru.verlyshev.fixtures.TestFixtures.generatePaymentDto;

class PaymentServiceImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PaymentServiceImpl paymentService;

    @Test
    @Sql(scripts = "/data/sql/payments-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data/sql/cleanup-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getByIdTest() {
        var result = paymentService.getPaymentById(UUID.fromString(EXISTING_GUID));

        assertThat(result.guid()).isEqualTo(UUID.fromString(EXISTING_GUID));
    }

    @Test
    void getByIdNotFoundTest() {
        assertThrows(EntityNotFoundException.class, () -> paymentService.getPaymentById(UUID.fromString(NOT_EXISTING_GUID)));
    }

    @Test
    void searchPagedTest() {
        var currency = "USD";
        var minAmount = new BigDecimal("50.00");
        var maxAmount = new BigDecimal("200.00");
        var createdAfter = OffsetDateTime.parse("2025-01-01T00:00:00+00:00");
        var createdBefore = OffsetDateTime.parse("2025-01-31T23:59:59+00:00");
        var status = PaymentStatus.APPROVED;

        var filterDto = new PaymentFilterDto(
                currency,
                minAmount,
                maxAmount,
                createdAfter,
                createdBefore,
                status,
                null,
                null
        );

        var pageable = PageRequest.of(0, 10);
        var result = paymentService.searchPaged(filterDto, pageable);

        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getContent()).isNotEmpty(),
                () -> assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(1),
                () -> assertThat(result.getNumber()).isZero(),
                () -> assertThat(result.getSize()).isEqualTo(10),
                () -> assertThat(result.isFirst()).isTrue()
        );

        assertThat(result.getContent()).allSatisfy(payment -> {
            assertThat(payment.currency()).isEqualTo(currency);
            assertThat(payment.status()).isEqualTo(status);
            assertThat(payment.amount())
                    .isGreaterThanOrEqualTo(minAmount)
                    .isLessThanOrEqualTo(maxAmount);
            assertThat(payment.createdAt())
                    .isAfterOrEqualTo(createdAfter)
                    .isBeforeOrEqualTo(createdBefore);
        });
    }

    @Test
    @Sql(scripts = "/data/sql/payments-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data/sql/cleanup-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deletePaymentTest() {
        paymentService.delete(UUID.fromString(EXISTING_GUID));

        assertThrows(EntityNotFoundException.class, () -> paymentService.getPaymentById(UUID.fromString(NOT_EXISTING_GUID)));
    }

    @Test
    void deletePaymentNotFountTest() {
        assertThrows(EntityNotFoundException.class, () -> paymentService.delete(UUID.fromString(EXISTING_GUID)));
    }

    @Test
    void createPaymentTest() {
        var paymentDto = generatePaymentDto();
        var result = paymentService.create(paymentDto);

        assertThat(result.guid()).isNotNull();
        checkPaymentDto(result, paymentDto);
    }

    @Test
    @Sql(scripts = "/data/sql/payments-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data/sql/cleanup-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updatePaymentTest() {
        var paymentDto = generatePaymentDto();
        var result = paymentService.update(UUID.fromString(EXISTING_GUID), paymentDto);

        assertThat(result.guid()).isEqualTo(UUID.fromString(EXISTING_GUID));
        checkPaymentDto(result, paymentDto);
    }

    @Test
    void updatePaymentNotFountTest() {
        var paymentDto = generatePaymentDto();

        assertThrows(EntityNotFoundException.class, () -> paymentService.update(UUID.fromString(NOT_EXISTING_GUID), paymentDto));
    }

    @Test
    @Sql(scripts = "/data/sql/payments-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data/sql/cleanup-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateNoteTest() {
        var newNote = "new note";
        var result = paymentService.updateNote(UUID.fromString(EXISTING_GUID), newNote);

        assertThat(result.note()).isEqualTo(newNote);
    }

    @Test
    void updateNoteNotFountTest() {
        var newNote = "new note";

        assertThrows(EntityNotFoundException.class, () -> paymentService.updateNote(UUID.fromString(NOT_EXISTING_GUID), newNote));
    }
}