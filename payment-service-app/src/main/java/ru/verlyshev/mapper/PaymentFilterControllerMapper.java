package ru.verlyshev.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.verlyshev.dto.PaymentFilterDto;
import ru.verlyshev.request.PaymentFilterRequest;
import ru.verlyshev.model.PaymentFilterCriteria;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PaymentFilterControllerMapper {
    PaymentFilterDto toDto(PaymentFilterRequest filter);
    PaymentFilterRequest fromDto(PaymentFilterDto filterDto);

}
