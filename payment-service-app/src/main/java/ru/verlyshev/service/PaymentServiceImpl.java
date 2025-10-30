package ru.verlyshev.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.dto.PaymentFilterDto;
import ru.verlyshev.dto.enums.OperationType;
import ru.verlyshev.exception.EntityNotFoundException;
import ru.verlyshev.integration.mapper.XPaymentMessageMapper;
import ru.verlyshev.integration.xpayment.async.producer.XPaymentAsyncProducer;
import ru.verlyshev.integration.xpayment.dto.XPaymentMessage;
import ru.verlyshev.integration.xpayment.enums.XPaymentStatus;
import ru.verlyshev.mapper.PaymentFilterPersistenceMapper;
import ru.verlyshev.mapper.PaymentPersistenceMapper;
import ru.verlyshev.persistence.entity.PaymentStatus;
import ru.verlyshev.persistence.repository.PaymentRepository;
import ru.verlyshev.persistence.specifications.PaymentFilterFactory;

import java.util.UUID;

import static ru.verlyshev.exception.ExceptionMessages.PAYMENT_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentFilterPersistenceMapper paymentFilterPersistenceMapper;
    private final PaymentPersistenceMapper paymentPersistenceMapper;

    private final XPaymentAsyncProducer producer;
    private final XPaymentMessageMapper messageMapper;

    @Override
    public PaymentDto getPaymentById(UUID guid) {
        return paymentRepository
            .findById(guid)
            .map(paymentPersistenceMapper::fromPaymentEntity)
            .orElseThrow(() ->
            new EntityNotFoundException(PAYMENT_NOT_FOUND.formatted(guid), guid, OperationType.FIND));
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

        final var message = messageMapper.toMessage(paymentEntity);
        message.toBuilder().messageId(UUID.randomUUID().toString()).build();
        producer.send(message);

        final var saved = paymentRepository.save(paymentEntity);
        return paymentPersistenceMapper.fromPaymentEntity(saved);
    }

    @Transactional
    public void changeStatus(String id, PaymentStatus status) {
        final var guid = UUID.fromString(id);

        paymentRepository.findByIdWithLock(guid)
                .ifPresentOrElse(
                        paymentEntity -> {
                            paymentEntity.setStatus(status);
                            paymentRepository.save(paymentEntity);
                            log.info("Payment {} status updated to: {}", guid, status);
                        },
                        () -> log.warn("Payment with id {} not found", guid)
                );
    }

    @Override
    @Transactional
    public PaymentDto update(UUID id, PaymentDto paymentDto) {
        final var existing = paymentRepository.findByIdWithLock(id)
            .orElseThrow(() -> new EntityNotFoundException(PAYMENT_NOT_FOUND.formatted(id), id, OperationType.UPDATE));

        paymentPersistenceMapper.updatePaymentEntityFromDto(paymentDto, existing);

        final var saved = paymentRepository.save(existing);
        return paymentPersistenceMapper.fromPaymentEntity(saved);
    }

    @Override
    public void delete(UUID id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException(PAYMENT_NOT_FOUND.formatted(id), id, OperationType.DELETE);
        }

        paymentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PaymentDto updateNote(UUID id, String note) {
        final var existing = paymentRepository.findByIdWithLock(id)
            .orElseThrow(() -> new EntityNotFoundException(PAYMENT_NOT_FOUND.formatted(id), id, OperationType.UPDATE));

        existing.setNote(note);
        final var saved = paymentRepository.save(existing);
        return paymentPersistenceMapper.fromPaymentEntity(saved);
    }
}
