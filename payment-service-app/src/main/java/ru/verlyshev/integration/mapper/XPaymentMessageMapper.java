package ru.verlyshev.integration.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.verlyshev.integration.xpayment.dto.XPaymentMessage;
import ru.verlyshev.integration.xpayment.enums.XPaymentStatus;
import ru.verlyshev.persistence.entity.Payment;
import ru.verlyshev.persistence.entity.PaymentStatus;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface XPaymentMessageMapper {

    @Mapping(target = "messageId", ignore = true)
    @Mapping(target = "paymentId", source = "guid")
    XPaymentMessage toMessage(Payment payment);

    default XPaymentStatus toXPaymentStatus(PaymentStatus status) {
        return switch (status) {
            case RECEIVED -> XPaymentStatus.PROCESSING;
            case APPROVED -> XPaymentStatus.PROCESSED;
            default -> XPaymentStatus.CANCELED;
        };
    }
}
