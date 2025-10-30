package ru.verlyshev.integration.xpayment.async.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.verlyshev.async.MessageHandler;
import ru.verlyshev.integration.xpayment.dto.XPaymentMessage;
import ru.verlyshev.integration.xpayment.enums.XPaymentStatus;
import ru.verlyshev.persistence.entity.PaymentStatus;
import ru.verlyshev.persistence.repository.PaymentRepository;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class XPaymentMessageHandler implements MessageHandler<XPaymentMessage> {

    private final PaymentRepository paymentRepository;

    @Override
    public void processMessage(XPaymentMessage message) {
        log.info("Received XPayment message: {}", message);

        final var status = message.status();

        if (!status.equals(XPaymentStatus.PROCESSING)) {
            final var paymentId = UUID.fromString(message.paymentId());

            paymentRepository.findById(paymentId)
                .ifPresentOrElse(payment -> {
                    final var newStatus = switch (status) {
                        case SUCCEEDED -> PaymentStatus.APPROVED;
                        case CANCELED -> PaymentStatus.DECLINED;
                        default -> {
                            log.warn("Unsupported XPaymentStatus {}", status);
                            yield PaymentStatus.NOT_SENT;
                        }
                    };

                    payment.setStatus(newStatus);
                    paymentRepository.save(payment);
                    log.info("Payment {} status updated to {}", payment, status);
                }, () -> log.warn("Payment {} not found", paymentId));
        }
    }
}
