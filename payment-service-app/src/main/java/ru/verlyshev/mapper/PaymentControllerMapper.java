package ru.verlyshev.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.response.PaymentResponse;
import ru.verlyshev.persistence.entity.Payment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PaymentControllerMapper {
    PaymentResponse toResponse(PaymentDto paymentDto);
    PaymentDto fromResponse(PaymentResponse response);
}
