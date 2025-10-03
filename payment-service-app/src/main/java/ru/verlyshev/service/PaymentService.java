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

    public List<Payment> search(PaymentFilter filter) {
        final var spec = PaymentFilterFactory.fromFilter(filter);
        final var sort = PaymentFilterFactory.getSort(filter);
        return paymentRepository.findAll(spec, sort);
    }

    public Page<Payment> searchPaged(PaymentFilter filter, Pageable pageable) {
        final var spec = PaymentFilterFactory.fromFilter(filter);
        return paymentRepository.findAll(spec, pageable);
    }
}
