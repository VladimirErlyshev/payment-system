package mapper;

import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.mapper.PaymentPersistenceMapper;
import ru.verlyshev.persistence.entity.Payment;
import ru.verlyshev.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentPersistenceMapperTest {
    private final PaymentPersistenceMapper mapper = Mappers.getMapper(PaymentPersistenceMapper.class);
    private UUID id;
    private UUID transactionId;
    private UUID inquiryRefId;
    private OffsetDateTime createDate;
    private OffsetDateTime currentDate;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String note;

    @BeforeEach
    void init() {
        id = UUID.randomUUID();
        transactionId = UUID.randomUUID();
        inquiryRefId = UUID.randomUUID();
        createDate = OffsetDateTime.now().minusDays(1);
        currentDate = OffsetDateTime.now();
        amount = new BigDecimal("123.45");
        currency = "RUB";
        status = PaymentStatus.APPROVED;
        note = "TEST NOTE";
    }

    @Test
    void shouldMapToDto() {
        //given
        var payment = Payment.builder()
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

        //when
        var paymentDto = mapper.fromPaymentEntity(payment);

        //then
        assertThat(paymentDto).isNotNull();
        assertThat(paymentDto.guid()).isEqualTo(id);
        assertThat(paymentDto.inquiryRefId()).isEqualTo(inquiryRefId);
        assertThat(paymentDto.amount()).isEqualTo(amount);
        assertThat(paymentDto.currency()).isEqualTo(currency);
        assertThat(paymentDto.transactionRefId()).isEqualTo(transactionId);
        assertThat(paymentDto.status()).isEqualTo(status);
        assertThat(paymentDto.note()).isEqualTo(note);
        assertThat(paymentDto.createdAt()).isEqualTo(createDate);
        assertThat(paymentDto.updatedAt()).isEqualTo(currentDate);
    }

    @Test
    void shouldMapToEntity() {
        //given
        var paymentDto = new PaymentDto(
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

        //when
        var payment = mapper.toPaymentEntity(paymentDto);

        //then
        assertThat(payment).isNotNull();
        assertThat(payment.getGuid()).isEqualTo(id);
        assertThat(payment.getInquiryRefId()).isEqualTo(inquiryRefId);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getCurrency()).isEqualTo(currency);
        assertThat(payment.getTransactionRefId()).isEqualTo(transactionId);
        assertThat(payment.getStatus()).isEqualTo(status);
        assertThat(payment.getNote()).isEqualTo(note);
        assertThat(payment.getCreatedAt()).isEqualTo(createDate);
        assertThat(payment.getUpdatedAt()).isEqualTo(currentDate);
    }
}
