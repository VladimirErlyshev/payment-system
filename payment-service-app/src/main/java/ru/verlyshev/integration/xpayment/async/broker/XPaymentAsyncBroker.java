package ru.verlyshev.integration.xpayment.async.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.verlyshev.async.AsyncBroker;
import ru.verlyshev.async.AsyncListener;
import ru.verlyshev.integration.xpayment.dto.XPaymentMessage;
import ru.verlyshev.integration.xpayment.enums.XPaymentStatus;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    @Scheduled(fixedDelay = 30000)
    public void sendMessage() {
        try {
            if (!queue.isEmpty()) {
                final var value = queue.poll();
                final var message = objectMapper.readValue(value, XPaymentMessage.class);
                message.toBuilder()
                    .status(isEven(message.amount()) ? XPaymentStatus.SUCCEEDED : XPaymentStatus.CANCELED)
                    .build();

                listener.onMessage(message);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isEven(BigDecimal value) {
        if (value.stripTrailingZeros().scale() > 0) {
            return false;
        }

        return value.toBigInteger()
                .remainder(BigInteger.valueOf(2))
                .equals(BigInteger.ZERO);
    }
}
