package ru.verlyshev.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.verlyshev.response.PaymentResponse;
import ru.verlyshev.request.PaymentFilterRequest;
import ru.verlyshev.mapper.PaymentFilterMapper;
import ru.verlyshev.mapper.PaymentMapper;
import ru.verlyshev.persistence.repository.PaymentRepository;
import ru.verlyshev.persistence.specifications.PaymentFilterFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentFilterMapper paymentFilterMapper;

    public List<PaymentResponse> search(PaymentFilterRequest filter) {
        final var entityFilter = paymentFilterMapper.fromRequest(filter);
        final var spec = PaymentFilterFactory.fromFilter(entityFilter);
        final var sort = PaymentFilterFactory.getSort(entityFilter);
        return paymentRepository.findAll(spec, sort).stream().map(paymentMapper::toResponse).toList();
    }

    public Page<PaymentResponse> searchPaged(PaymentFilterRequest filter, Pageable pageable) {
        final var entityFilter = paymentFilterMapper.fromRequest(filter);
        final var spec = PaymentFilterFactory.fromFilter(entityFilter);
        return paymentRepository.findAll(spec, pageable).map(paymentMapper::toResponse);
    }
}
