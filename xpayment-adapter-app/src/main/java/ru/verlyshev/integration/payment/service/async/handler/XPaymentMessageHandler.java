package ru.verlyshev.integration.payment.service.async.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.verlyshev.async.MessageHandler;
import ru.verlyshev.integration.payment.service.dto.XPaymentMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class XPaymentMessageHandler implements MessageHandler<XPaymentMessage> {

    private final ApplicationContext applicationContext;

    @Override
    public void processMessage(XPaymentMessage message) {
        log.info("Received XPayment message: {}", message);
    }
}
