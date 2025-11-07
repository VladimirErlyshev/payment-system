package ru.verlyshev.async;

public interface AsyncListener<T extends AsyncMessage> {

    void onMessage(T message);
}
