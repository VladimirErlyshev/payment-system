package ru.verlyshev.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.verlyshev.dto.PaymentFilterDto;
import ru.verlyshev.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    List<PaymentResponse> search(PaymentFilterDto filter);

    Page<PaymentResponse> searchPaged(PaymentFilterDto filter, Pageable pageable);
}
