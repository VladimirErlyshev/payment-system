package ru.verlyshev.integration.xpayment.async.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.verlyshev.async.AsyncProducer;
import ru.verlyshev.integration.xpayment.dto.XPaymentMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class XPaymentAsyncProducer implements AsyncProducer<XPaymentMessage> {

    private final KafkaTemplate<String, XPaymentMessage> kafkaTemplate;

    @Value("${app.kafka.topics.xpayment-adapter.request}")
    private String topic;

    @Override
    public void send(XPaymentMessage message) {
        try {
            log.info("Sending message: {}", message);
            kafkaTemplate.send(topic, null, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
