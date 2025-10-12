package ru.verlyshev.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.verlyshev.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentRequest(
        @NotNull
        UUID inquiryRefId,

        @NotNull
        @Positive
        BigDecimal amount,

        @NotNull
        @Size(min = 3, max = 3)
        String currency,

        UUID transactionRefId,

        @NotNull
        PaymentStatus status,

        String note
) {}
