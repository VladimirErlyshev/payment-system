package ru.verlyshev.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.verlyshev.request.PaymentFilterRequest;
import ru.verlyshev.model.PaymentFilterCriteria;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentFilterMapper {
    PaymentFilterRequest toRequest(PaymentFilterCriteria filter);
    PaymentFilterCriteria fromRequest(PaymentFilterRequest filterDto);
}
