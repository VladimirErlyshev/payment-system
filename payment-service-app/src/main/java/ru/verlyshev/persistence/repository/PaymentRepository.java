package ru.verlyshev.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
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

    Optional<Payment> findFirstByOrderByAmountAsc();

    Optional<Payment> findFirstByOrderByAmountDesc();

    List<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    List<Payment> findByCreatedAtBefore(OffsetDateTime dateTime);

    List<Payment> findByCreatedAtAfter(OffsetDateTime dateTime);

    List<Payment> findByCreatedAtBetween(OffsetDateTime startDateTime, OffsetDateTime endDateTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.guid = :guid")
    Optional<Payment> findByIdWithLock(UUID guid);
}
