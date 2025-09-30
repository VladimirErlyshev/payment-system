package ru.verlyshev.persistence.specifications;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.verlyshev.model.PaymentFilter;
import ru.verlyshev.persistence.entity.Payment;

import java.util.Objects;

@Component
public class PaymentFilterFactory {
    public Specification<Payment> fromFilter(PaymentFilter filter) {
        var spec = Specification.<Payment>unrestricted();
        if (StringUtils.hasText(filter.currency())) {
            spec = spec.and(PaymentSpecifications.hasCurrency(filter.currency()));
        }

        if (Objects.nonNull(filter.minAmount()) && Objects.nonNull(filter.maxAmount())) {
            spec = spec.and(PaymentSpecifications.amountBetween(filter.minAmount(), filter.maxAmount()));
        }

        if (Objects.nonNull(filter.createdAfter()) && Objects.nonNull(filter.createdBefore())) {
            spec = spec.and(PaymentSpecifications.createdBetween(filter.createdAfter(), filter.createdBefore()));
        }

        if (Objects.nonNull(filter.status())) {
            spec = spec.and(PaymentSpecifications.hasStatus(filter.status()));
        }

        return spec;
    }

    public Sort getSort(PaymentFilter filter) {
        if (!StringUtils.hasText(filter.sortBy())) {
            return Sort.unsorted();
        }

        final var sortBy = filter.sortBy();
        var direction = Sort.Direction.ASC;

        if (StringUtils.hasText(filter.sortDirection()) && filter.sortDirection().equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        if (sortBy.equalsIgnoreCase("amount") || sortBy.equalsIgnoreCase("createdAt")) {
            return Sort.by(direction, sortBy);
        }

        return Sort.unsorted();
    }
}
