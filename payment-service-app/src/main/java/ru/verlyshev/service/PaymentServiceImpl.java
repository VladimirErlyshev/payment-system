package ru.verlyshev.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.dto.PaymentFilterDto;
import ru.verlyshev.mapper.PaymentFilterPersistenceMapper;
import ru.verlyshev.mapper.PaymentPersistenceMapper;
import ru.verlyshev.persistence.repository.PaymentRepository;
import ru.verlyshev.persistence.specifications.PaymentFilterFactory;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentFilterPersistenceMapper paymentFilterPersistenceMapper;
    private final PaymentPersistenceMapper paymentPersistenceMapper;

    @Override
    public PaymentDto findById(UUID id) {
        final var result = paymentRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: %s".formatted(id)));
        return paymentPersistenceMapper.fromPaymentEntity(result);
    }

    @Override
    public Page<PaymentDto> searchPaged(PaymentFilterDto filterDto, Pageable pageable) {
        final var criteriaFilter = paymentFilterPersistenceMapper.toFilterCriteria(filterDto);
        final var spec = PaymentFilterFactory.fromFilter(criteriaFilter);
        final var sort = PaymentFilterFactory.getSort(criteriaFilter);

        if (sort.isSorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        return paymentRepository.findAll(spec, pageable)
                .map(paymentPersistenceMapper::fromPaymentEntity);
    }

    @Override
    public PaymentDto create(PaymentDto paymentDto) {
        final var paymentEntity = paymentPersistenceMapper.toPaymentEntity(paymentDto);
        final var saved = paymentRepository.save(paymentEntity);
        return paymentPersistenceMapper.fromPaymentEntity(saved);
    }
}
