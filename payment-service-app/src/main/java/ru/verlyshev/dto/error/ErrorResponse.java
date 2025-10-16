package ru.verlyshev.dto.error;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ErrorResponse(
    UUID id,
    String error,
    Instant timestamp,
    String operation
) { }