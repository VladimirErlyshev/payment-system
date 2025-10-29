package ru.verlyshev.async;

public interface AsyncBroker {

    void receiveMessage(String data);

    void sendMessage();
}
