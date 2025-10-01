package ru.verlyshev.persistence.specifications;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import ru.verlyshev.model.PaymentFilter;
import ru.verlyshev.persistence.entity.Payment;

public class PaymentFilterFactory {
    public static Specification<Payment> fromFilter(PaymentFilter filter) {
        var spec = Specification.<Payment>unrestricted();
        if (StringUtils.hasText(filter.currency())) {
            spec = spec.and(PaymentSpecifications.hasCurrency(filter.currency()));
        }

        if (filter.minAmount() != null && filter.maxAmount() != null) {
            spec = spec.and(PaymentSpecifications.amountBetween(filter.minAmount(), filter.maxAmount()));
        }

        if (filter.createdAfter() != null && filter.createdBefore() != null) {
            spec = spec.and(PaymentSpecifications.createdBetween(filter.createdAfter(), filter.createdBefore()));
        }

        if (filter.status() != null) {
            spec = spec.and(PaymentSpecifications.hasStatus(filter.status()));
        }

        return spec;
    }

    public static Sort getSort(PaymentFilter filter) {
        final var sortBy = filter.sortBy();
        if (!StringUtils.hasText(sortBy)) {
            return Sort.unsorted();
        }
        var direction = Sort.Direction.ASC;

        final var sortDirection = filter.sortDirection();
        if (StringUtils.hasText(sortDirection) && sortDirection.equalsIgnoreCase(Sort.Direction.DESC.name())) {
            direction = Sort.Direction.DESC;
        }

        if (sortBy.equalsIgnoreCase("amount") || sortBy.equalsIgnoreCase("createdAt")) {
            return Sort.by(direction, sortBy);
        }

        return Sort.unsorted();
    }
}
