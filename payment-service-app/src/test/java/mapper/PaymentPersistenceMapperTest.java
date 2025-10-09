package mapper;

import configuration.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.verlyshev.mapper.PaymentPersistenceMapper;
import ru.verlyshev.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

class PaymentPersistenceMapperTest extends TestConfiguration {
    private final PaymentPersistenceMapper mapper = Mappers.getMapper(PaymentPersistenceMapper.class);

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
        var payment = generatePayment(id, transactionId, amount, currency,
                transactionId, status, note, createDate, currentDate);

        //when
        var paymentDto = mapper.fromPaymentEntity(payment);

        //then
        checkPaymentDto(paymentDto, payment);
    }

    @Test
    void shouldMapToEntity() {
        //given
        var paymentDto = generatePaymentDto(id, transactionId, amount, currency,
                transactionId, status, note, createDate, currentDate);

        //when
        var payment = mapper.toPaymentEntity(paymentDto);

        //then
        checkPayment(payment, paymentDto);
    }
}
