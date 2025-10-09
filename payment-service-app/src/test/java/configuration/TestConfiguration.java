package configuration;

import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.persistence.entity.Payment;
import ru.verlyshev.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class TestConfiguration {
    protected UUID id;
    protected UUID transactionId;
    protected UUID inquiryRefId;
    protected OffsetDateTime createDate;
    protected OffsetDateTime currentDate;
    protected BigDecimal amount;
    protected String currency;
    protected PaymentStatus status;
    protected String note;

    protected Payment generatePayment(UUID guid, UUID inquiryRefId, BigDecimal amount,
                                      String currency, UUID transactionRefId,
                                      PaymentStatus status, String note,
                                      OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return Payment.builder()
                .guid(guid)
                .inquiryRefId(inquiryRefId)
                .amount(amount)
                .currency(currency)
                .transactionRefId(transactionRefId)
                .status(status)
                .note(note)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    protected PaymentDto generatePaymentDto(UUID guid, UUID inquiryRefId, BigDecimal amount,
                                            String currency, UUID transactionRefId,
                                            PaymentStatus status, String note,
                                            OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new PaymentDto(
                guid,
                inquiryRefId,
                amount,
                currency,
                transactionRefId,
                status,
                note,
                createdAt,
                updatedAt
        );
    }

    protected void checkPaymentDto(PaymentDto paymentDto, Payment payment) {
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

    protected void checkPayment(Payment payment, PaymentDto paymentDto) {
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
}
