package ru.verlyshev.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.verlyshev.persistence.entity.Payment;
import ru.verlyshev.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {
    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByCurrency(String currency);

    Optional<Payment> findFirstOrderByAmountAsc();

    Optional<Payment> findFirstOrderByAmountDesc();

    List<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    List<Payment> findByCreatedAtBefore(OffsetDateTime dateTime);

    List<Payment> findByCreatedAtAfter(OffsetDateTime dateTime);

    List<Payment> findByCreatedAtBetween(OffsetDateTime startDateTime, OffsetDateTime endDateTime);
}
