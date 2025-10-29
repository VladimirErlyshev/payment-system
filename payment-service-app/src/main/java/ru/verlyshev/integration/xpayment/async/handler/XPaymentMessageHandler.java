package ru.verlyshev.integration.xpayment.async.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.verlyshev.async.MessageHandler;
import ru.verlyshev.integration.xpayment.dto.XPaymentMessage;

@Slf4j
@Component
public class XPaymentMessageHandler implements MessageHandler<XPaymentMessage> {

    @Override
    public void processMessage(XPaymentMessage message) {
        log.info("Received XPayment message: {}", message);
    }
}
