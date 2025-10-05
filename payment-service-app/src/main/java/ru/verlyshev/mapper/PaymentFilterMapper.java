package ru.verlyshev.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.verlyshev.dto.PaymentFilterDto;
import ru.verlyshev.model.PaymentFilter;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentFilterMapper {
    PaymentFilterDto toFilterDto(PaymentFilter filter);
    PaymentFilter toEntityFilter(PaymentFilterDto filterDto);
}
