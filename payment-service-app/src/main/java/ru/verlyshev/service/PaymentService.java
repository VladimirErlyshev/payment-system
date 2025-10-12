package ru.verlyshev.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.dto.PaymentFilterDto;

import java.util.UUID;

public interface PaymentService {
    PaymentDto findById(UUID id);

    Page<PaymentDto> searchPaged(PaymentFilterDto filter, Pageable pageable);

    PaymentDto create(PaymentDto paymentDto);
}
