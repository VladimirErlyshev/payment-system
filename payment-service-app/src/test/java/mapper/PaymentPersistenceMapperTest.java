package mapper;

import org.mapstruct.factory.Mappers;
import ru.verlyshev.mapper.PaymentPersistenceMapper;
import ru.verlyshev.persistence.entity.Payment;
import ru.verlyshev.persistence.entity.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentPersistenceMapperTest {
    private final PaymentPersistenceMapper mapper = Mappers.getMapper(PaymentPersistenceMapper.class);

    @Test
    void shouldMapToDto() {
        var id = UUID.randomUUID();
        var transactionId = UUID.randomUUID();
        var inquiryRefId = UUID.randomUUID();
        var date = OffsetDateTime.now();
        var payment = Payment.builder()
                .guid(id)
                .inquiryRefId(inquiryRefId)
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .transactionRefId(transactionId)
                .status(PaymentStatus.PENDING)
                .note("test")
                .createdAt(date)
                .updatedAt(date)
                .build();

        var paymentDto = mapper.fromPaymentEntity(payment);

        assertThat(paymentDto).isNotNull();
        assertThat(paymentDto.guid()).isEqualTo(id);
        assertThat(paymentDto.inquiryRefId()).isEqualTo(inquiryRefId);
        assertThat(paymentDto.amount()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(paymentDto.currency()).isEqualTo("USD");
        assertThat(paymentDto.transactionRefId()).isEqualTo(transactionId);
        assertThat(paymentDto.status()).isEqualTo(PaymentStatus.PENDING);
        assertThat(paymentDto.note()).isEqualTo("test");
        assertThat(paymentDto.createdAt()).isEqualTo(date);
        assertThat(paymentDto.updatedAt()).isEqualTo(date);
    }
}
