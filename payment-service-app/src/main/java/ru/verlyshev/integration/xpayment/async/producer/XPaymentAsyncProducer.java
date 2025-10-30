package ru.verlyshev.integration.xpayment.async.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.verlyshev.async.AsyncBroker;
import ru.verlyshev.async.AsyncProducer;
import ru.verlyshev.integration.xpayment.dto.XPaymentMessage;

@Component
@RequiredArgsConstructor
public class XPaymentAsyncProducer implements AsyncProducer<XPaymentMessage> {

    private final AsyncBroker broker;
    private final ObjectMapper objectMapper;

    @Override
    public void send(XPaymentMessage message) {
        try {
            broker.receiveMessage(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
