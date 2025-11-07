package ru.verlyshev.integration.payment.service.async.listener;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.verlyshev.async.AsyncListener;
import ru.verlyshev.async.MessageHandler;
import ru.verlyshev.integration.payment.service.dto.XPaymentMessage;

@Component
@RequiredArgsConstructor
public class XPaymentAsyncListener implements AsyncListener<XPaymentMessage> {

    private final MessageHandler<XPaymentMessage> messageHandler;

    @KafkaListener(topics = "${app.kafka.topics.xpayment-adapter.response}")
    public void consume(XPaymentMessage message,
                        ConsumerRecord<String, XPaymentMessage> consumerRecord,
                        Acknowledgment ack) {
        onMessage(message);
        ack.acknowledge();
    }

    @Override
    public void onMessage(XPaymentMessage message) {
        messageHandler.processMessage(message);
    }
}
