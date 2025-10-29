package ru.verlyshev.integration.xpayment.async.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.verlyshev.async.AsyncBroker;
import ru.verlyshev.async.AsyncListener;
import ru.verlyshev.integration.xpayment.dto.XPaymentMessage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class XPaymentAsyncBroker implements AsyncBroker {

    private final Queue<String> queue = new ConcurrentLinkedQueue<>();
    private final AsyncListener<XPaymentMessage> listener;
    private final ObjectMapper objectMapper;

    @Override
    public void receiveMessage(String data) {
        queue.add(data);
    }

    @Override
    @Scheduled(fixedDelay = 5000)
    public void sendMessage() {
        try {
            if (!queue.isEmpty()) {
                var value = queue.poll();
                var message = objectMapper.readValue(value, XPaymentMessage.class);
                listener.onMessage(message);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
