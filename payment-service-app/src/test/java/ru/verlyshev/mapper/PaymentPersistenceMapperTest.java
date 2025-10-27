package ru.verlyshev.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static ru.verlyshev.fixtures.TestFixtures.checkPayment;
import static ru.verlyshev.fixtures.TestFixtures.checkPaymentDto;
import static ru.verlyshev.fixtures.TestFixtures.generatePayment;
import static ru.verlyshev.fixtures.TestFixtures.generatePaymentDto;

class PaymentPersistenceMapperTest {
    private final PaymentPersistenceMapper mapper = Mappers.getMapper(PaymentPersistenceMapper.class);
    @Test
    void shouldMapToDto() {
        //given
        var payment = generatePayment();

        //when
        var paymentDto = mapper.fromPaymentEntity(payment);

        //then
        checkPaymentDto(paymentDto, payment);
    }

    @Test
    void shouldMapToEntity() {
        //given
        var paymentDto = generatePaymentDto();

        //when
        var payment = mapper.toPaymentEntity(paymentDto);

        //then
        checkPayment(payment, paymentDto);
    }
}
