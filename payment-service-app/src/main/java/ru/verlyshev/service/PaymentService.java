package ru.verlyshev.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.verlyshev.response.PaymentResponse;
import ru.verlyshev.request.PaymentFilterRequest;

import java.util.List;

public interface PaymentService {
    List<PaymentResponse> search(PaymentFilterRequest filter);

    Page<PaymentResponse> searchPaged(PaymentFilterRequest filter, Pageable pageable);
}
