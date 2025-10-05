package ru.verlyshev.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.persistence.entity.Payment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {
    PaymentDto toDto(Payment payment);
    Payment toEntity(PaymentDto dto);
}
