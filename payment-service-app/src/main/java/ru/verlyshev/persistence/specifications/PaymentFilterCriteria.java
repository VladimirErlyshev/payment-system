package ru.verlyshev.persistence.specifications;

import ru.verlyshev.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentFilterCriteria(
    String currency,
    BigDecimal minAmount,
    BigDecimal maxAmount,
    OffsetDateTime createdAfter,
    OffsetDateTime createdBefore,
    PaymentStatus status,
    String sortBy,
    String sortDirection
) { }