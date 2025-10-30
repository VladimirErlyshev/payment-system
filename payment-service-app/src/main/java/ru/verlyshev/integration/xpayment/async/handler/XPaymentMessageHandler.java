package ru.verlyshev.integration.xpayment.async.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.verlyshev.async.MessageHandler;
import ru.verlyshev.integration.xpayment.dto.XPaymentMessage;
import ru.verlyshev.integration.xpayment.enums.XPaymentStatus;
import ru.verlyshev.persistence.entity.PaymentStatus;
import ru.verlyshev.persistence.repository.PaymentRepository;
import ru.verlyshev.service.PaymentService;
import ru.verlyshev.service.PaymentServiceImpl;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class XPaymentMessageHandler implements MessageHandler<XPaymentMessage> {

    private final PaymentServiceImpl paymentService;

    @Override
    public void processMessage(XPaymentMessage message) {
        log.info("Received XPayment message: {}", message);

        final var paymentId = message.paymentId();
        final var xStatus = message.status();

        if (!xStatus.equals(XPaymentStatus.PROCESSING)) {
            final var paymentStatus = switch (xStatus) {
                case SUCCEEDED -> PaymentStatus.APPROVED;
                case CANCELED -> PaymentStatus.DECLINED;
                default -> throw new IllegalStateException("Unexpected status: " + xStatus);
            };

            paymentService.changeStatus(paymentId, paymentStatus);
        }
    }
}
