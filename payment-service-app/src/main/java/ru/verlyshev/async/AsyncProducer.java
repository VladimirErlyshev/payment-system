package ru.verlyshev.async;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface AsyncProducer<T extends AsyncMessage> {

    void send(T message) throws JsonProcessingException;
}
