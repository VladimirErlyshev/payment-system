package ru.verlyshev.dto;

import ru.verlyshev.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentFilterDto(
    String currency,
    BigDecimal minAmount,
    BigDecimal maxAmount,
    OffsetDateTime createdAfter,
    OffsetDateTime createdBefore,
    PaymentStatus status,
    String sortBy,
    String sortDirection
) { }
