package ru.verlyshev.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.dto.request.PaymentRequest;
import ru.verlyshev.dto.response.PaymentResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PaymentControllerMapper {
    PaymentResponse toResponse(PaymentDto paymentDto);
    PaymentDto fromResponse(PaymentResponse response);


    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentDto fromRequest(PaymentRequest request);
}
