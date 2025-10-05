package ru.verlyshev.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.dto.PaymentFilterDto;

import java.util.List;

public interface PaymentService {
    List<PaymentDto> search(PaymentFilterDto filter);
    Page<PaymentDto> searchPaged(PaymentFilterDto filter, Pageable pageable);
}
