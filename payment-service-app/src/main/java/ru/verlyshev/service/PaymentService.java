package ru.verlyshev.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.dto.PaymentFilterDto;
import ru.verlyshev.persistence.entity.PaymentStatus;

import java.util.UUID;

public interface PaymentService {
    PaymentDto getPaymentById(UUID guid);

    Page<PaymentDto> searchPaged(PaymentFilterDto filter, Pageable pageable);

    PaymentDto create(PaymentDto paymentDto);

    void changeStatus(String id, PaymentStatus status);

    PaymentDto update(UUID id, PaymentDto paymentDto);

    void delete(UUID id);

    PaymentDto updateNote(UUID id, String note);
}
