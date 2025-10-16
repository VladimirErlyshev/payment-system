package ru.verlyshev.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionMessages {
    public static final String PAYMENT_NOT_FOUND = "Payment not found with id: %s";
}
