package ru.verlyshev.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.verlyshev.dto.error.ErrorResponse;
import ru.verlyshev.exception.EntityNotFoundException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(EntityNotFoundException e) {
        return ErrorResponse.builder()
                .error(e.getMessage())
                .id(e.getId())
                .timestamp(Instant.now())
                .operation(e.getOperation().name())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOther(Exception e) {
        return ErrorResponse.builder()
                .error(e.getMessage())
                .id(null)
                .timestamp(Instant.now())
                .operation(null)
                .build();
    }
}
