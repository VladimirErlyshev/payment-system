package ru.verlyshev.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.dto.request.PaymentRequest;
import ru.verlyshev.persistence.entity.Payment;
import ru.verlyshev.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestFixtures {
    public static UUID id = UUID.randomUUID();
    public static UUID transactionId = UUID.randomUUID();
    public static UUID inquiryRefId = UUID.randomUUID();
    public static OffsetDateTime createDate = OffsetDateTime.now().minusDays(1);
    public static OffsetDateTime currentDate = OffsetDateTime.now();
    public static BigDecimal amount = generateRandomAmount();
    public static String currency = generateRandomCurrency();
    public static PaymentStatus status = getRandomStatus();
    public static String note = RandomStringUtils.randomAlphabetic(10);

    public static Payment generatePayment() {
        return Payment.builder()
                .guid(id)
                .inquiryRefId(inquiryRefId)
                .amount(amount)
                .currency(currency)
                .transactionRefId(transactionId)
                .status(status)
                .note(note)
                .createdAt(createDate)
                .updatedAt(currentDate)
                .build();
    }

    public static PaymentDto generatePaymentDto() {
        return new PaymentDto(
                id,
                inquiryRefId,
                amount,
                currency,
                transactionId,
                status,
                note,
                createDate,
                currentDate
        );
    }

    public static PaymentRequest generatePaymentRequest() {
        return new PaymentRequest(
                inquiryRefId,
                amount,
                currency,
                transactionId,
                status,
                note
        );
    }

    public static void checkPaymentDto(PaymentDto paymentDto, Payment payment) {
        assertThat(paymentDto).isNotNull();
        assertThat(paymentDto.guid()).isEqualTo(payment.getGuid());
        assertThat(paymentDto.inquiryRefId()).isEqualTo(payment.getInquiryRefId());
        assertThat(paymentDto.amount()).isEqualTo(payment.getAmount());
        assertThat(paymentDto.currency()).isEqualTo(payment.getCurrency());
        assertThat(paymentDto.transactionRefId()).isEqualTo(payment.getTransactionRefId());
        assertThat(paymentDto.status()).isEqualTo(payment.getStatus());
        assertThat(paymentDto.note()).isEqualTo(payment.getNote());
        assertThat(paymentDto.createdAt()).isEqualTo(payment.getCreatedAt());
        assertThat(paymentDto.updatedAt()).isEqualTo(payment.getUpdatedAt());
    }

    public static void checkPayment(Payment payment, PaymentDto paymentDto) {
        assertThat(payment).isNotNull();
        assertThat(payment.getGuid()).isEqualTo(paymentDto.guid());
        assertThat(payment.getInquiryRefId()).isEqualTo(paymentDto.inquiryRefId());
        assertThat(payment.getAmount()).isEqualTo(paymentDto.amount());
        assertThat(payment.getCurrency()).isEqualTo(paymentDto.currency());
        assertThat(payment.getTransactionRefId()).isEqualTo(paymentDto.transactionRefId());
        assertThat(payment.getStatus()).isEqualTo(paymentDto.status());
        assertThat(payment.getNote()).isEqualTo(paymentDto.note());
        assertThat(payment.getCreatedAt()).isEqualTo(paymentDto.createdAt());
        assertThat(payment.getUpdatedAt()).isEqualTo(paymentDto.updatedAt());
    }

    public static ResultMatcher[] paymentResponseMatchers(PaymentRequest request, UUID expectedGuid) {
        return new ResultMatcher[]{
                jsonPath(JsonPathField.GUID).value(expectedGuid.toString()),
                jsonPath(JsonPathField.INQUIRY_REF_ID).value(request.inquiryRefId().toString()),
                jsonPath(JsonPathField.AMOUNT).value(request.amount().doubleValue()),
                jsonPath(JsonPathField.CURRENCY).value(request.currency()),
                jsonPath(JsonPathField.TRANSACTION_REF_ID).value(request.transactionRefId().toString()),
                jsonPath(JsonPathField.STATUS).value(request.status().name()),
                jsonPath(JsonPathField.NOTE).value(request.note())
        };
    }

    public static ResultMatcher[] paymentResponseMatchers(PaymentRequest request) {
        return new ResultMatcher[]{
                jsonPath(JsonPathField.INQUIRY_REF_ID).value(request.inquiryRefId().toString()),
                jsonPath(JsonPathField.AMOUNT).value(request.amount().doubleValue()),
                jsonPath(JsonPathField.CURRENCY).value(request.currency()),
                jsonPath(JsonPathField.TRANSACTION_REF_ID).value(request.transactionRefId().toString()),
                jsonPath(JsonPathField.STATUS).value(request.status().name()),
                jsonPath(JsonPathField.NOTE).value(request.note())
        };
    }

    private static BigDecimal generateRandomAmount() {
        var cents = ThreadLocalRandom.current().nextInt(100000);
        return BigDecimal.valueOf(cents, 2);
    }

    private static String generateRandomCurrency() {
        return ThreadLocalRandom.current()
                .ints(3, 'A', 'Z' + 1)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }

    private static PaymentStatus getRandomStatus() {
        var index = ThreadLocalRandom.current().nextInt(PaymentStatus.values().length);
        return PaymentStatus.values()[index];
    }
}
