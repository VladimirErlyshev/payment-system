package ru.verlyshev.integration.xpayment.dto;

import lombok.Builder;
import ru.verlyshev.async.AsyncMessage;
import ru.verlyshev.integration.xpayment.enums.XPaymentStatus;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record XPaymentMessage(
    String messageId,
    String paymentId,
    BigDecimal amount,
    String currency,
    XPaymentStatus status
) implements AsyncMessage { }
