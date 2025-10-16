package ru.verlyshev.exception;

import lombok.Getter;
import ru.verlyshev.dto.enums.OperationType;

import java.util.UUID;

@Getter
public class EntityNotFoundException extends BasePaymentServiceException {
    private final UUID id;
    private final OperationType operation;

    public EntityNotFoundException(String message, UUID id, OperationType operation) {
        super(message);
        this.id = id;
        this.operation = operation;
    }
}
