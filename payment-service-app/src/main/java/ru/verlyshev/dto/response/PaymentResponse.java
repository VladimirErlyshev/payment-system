package ru.verlyshev.dto.response;

import ru.verlyshev.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentResponse(
    UUID guid,
    UUID inquiryRefId,
    BigDecimal amount,
    String currency,
    UUID transactionRefId,
    PaymentStatus status,
    String note,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) { }
