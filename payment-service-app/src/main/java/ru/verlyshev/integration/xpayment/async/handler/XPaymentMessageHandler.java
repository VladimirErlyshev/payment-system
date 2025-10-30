package ru.verlyshev.integration.xpayment.async.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.verlyshev.async.MessageHandler;
import ru.verlyshev.integration.xpayment.dto.XPaymentMessage;
import ru.verlyshev.integration.xpayment.enums.XPaymentStatus;
import ru.verlyshev.persistence.entity.PaymentStatus;
import ru.verlyshev.service.PaymentService;

@Slf4j
@Component
@RequiredArgsConstructor
public class XPaymentMessageHandler implements MessageHandler<XPaymentMessage> {

    private final ApplicationContext applicationContext;

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

            final var paymentService = applicationContext.getBean(PaymentService.class);
            paymentService.changeStatus(paymentId, paymentStatus);
        }
    }
}
