package ru.verlyshev.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.dto.PaymentFilterDto;
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

    public List<PaymentDto> search(PaymentFilterDto filter) {
        final var spec = PaymentFilterFactory.fromFilter(paymentFilterMapper.toEntityFilter(filter));
        final var sort = PaymentFilterFactory.getSort(paymentFilterMapper.toEntityFilter(filter));
        return paymentRepository.findAll(spec, sort).stream().map(paymentMapper::toDto).toList();
    }

    public Page<PaymentDto> searchPaged(PaymentFilterDto filter, Pageable pageable) {
        final var spec = PaymentFilterFactory.fromFilter(paymentFilterMapper.toEntityFilter(filter));
        return paymentRepository.findAll(spec, pageable).map(paymentMapper::toDto);
    }
}
