package ru.verlyshev.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.persistence.entity.Payment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PaymentPersistenceMapper {
    Payment toPaymentEntity(PaymentDto response);
    PaymentDto fromPaymentEntity(Payment payment);

    void updatePaymentEntityFromDto(PaymentDto dto, @MappingTarget Payment entity);
}
