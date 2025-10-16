package ru.verlyshev.exception;

public class BasePaymentServiceException extends RuntimeException{
    public BasePaymentServiceException(String message) {
        super(message);
    }

    public BasePaymentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
