package ru.verlyshev.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.verlyshev.dto.PaymentFilterDto;
import ru.verlyshev.model.PaymentFilterCriteria;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PaymentFilterPersistenceMapper {
    PaymentFilterCriteria toFilterCriteria(PaymentFilterDto filterDto);
    PaymentFilterDto fromFilterCriteria(PaymentFilterCriteria filter);
}
