package ru.verlyshev.service;

import jakarta.persistence.EntityNotFoundException;
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
    public PaymentDto getPaymentById(UUID guid) {
        final var result = paymentRepository
                .findById(guid)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: %s".formatted(guid)));
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

    @Override
    public PaymentDto update(UUID id, PaymentDto paymentDto) {
        final var existing = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: %s".formatted(id)));

        final var updated = paymentPersistenceMapper.toPaymentEntity(paymentDto);
        updated.setGuid(existing.getGuid());
        updated.setCreatedAt(existing.getCreatedAt());

        final var saved = paymentRepository.save(updated);
        return paymentPersistenceMapper.fromPaymentEntity(saved);
    }

    @Override
    public void delete(UUID id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Payment not found with id: %s".formatted(id));
        }

        paymentRepository.deleteById(id);
    }

    @Override
    public PaymentDto updateNote(UUID id, String note) {
        final var existing = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: %s".formatted(id)));

        existing.setNote(note);
        final var saved = paymentRepository.save(existing);
        return paymentPersistenceMapper.fromPaymentEntity(saved);
    }
}
