package ru.verlyshev.integration.xpayment.async.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.verlyshev.async.AsyncListener;
import ru.verlyshev.async.MessageHandler;
import ru.verlyshev.integration.xpayment.dto.XPaymentMessage;

@Component
@RequiredArgsConstructor
public class XPaymentAsyncListener implements AsyncListener<XPaymentMessage> {

    private final MessageHandler<XPaymentMessage> messageHandler;

    @Override
    public void onMessage(XPaymentMessage message) {
        messageHandler.processMessage(message);
    }
}
