package ru.verlyshev.async;

public interface MessageHandler<T extends AsyncMessage> {

    void processMessage(T message);
}
