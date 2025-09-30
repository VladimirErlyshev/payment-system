package ru.verlyshev.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.verlyshev.model.PaymentFilter;
import ru.verlyshev.persistence.entity.Payment;
import ru.verlyshev.persistence.repository.PaymentRepository;
import ru.verlyshev.persistence.specifications.PaymentFilterFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentFilterFactory paymentFilterFactory;

    public List<Payment> search(PaymentFilter filter) {
        final var spec = paymentFilterFactory.fromFilter(filter);
        final var sort = paymentFilterFactory.getSort(filter);
        return paymentRepository.findAll(spec, sort);
    }

    public Page<Payment> searchPaged(PaymentFilter filter, Pageable pageable) {
        final var spec = paymentFilterFactory.fromFilter(filter);
        return paymentRepository.findAll(spec, pageable);
    }
}
