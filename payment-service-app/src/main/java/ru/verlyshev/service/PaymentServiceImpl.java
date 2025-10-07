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
import ru.verlyshev.mapper.PaymentControllerMapper;
import ru.verlyshev.persistence.repository.PaymentRepository;
import ru.verlyshev.persistence.specifications.PaymentFilterFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentControllerMapper paymentControllerMapper;
    private final PaymentFilterPersistenceMapper paymentFilterPersistenceMapper;
    private final PaymentPersistenceMapper paymentPersistenceMapper;

    @Override
    public List<PaymentDto> search(PaymentFilterDto filterDto) {
        final var criteriaFilter = paymentFilterPersistenceMapper.toFilterCriteria(filterDto);
        final var spec = PaymentFilterFactory.fromFilter(criteriaFilter);
        final var sort = PaymentFilterFactory.getSort(criteriaFilter);

        return paymentRepository.findAll(spec, sort)
                .stream()
                .map(paymentPersistenceMapper::fromPaymentEntity)
                .toList();
    }

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
}
