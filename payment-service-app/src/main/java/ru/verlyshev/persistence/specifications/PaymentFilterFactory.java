package ru.verlyshev.persistence.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import ru.verlyshev.persistence.entity.Payment;
import ru.verlyshev.persistence.entity.Payment_;

import java.util.ArrayList;

public class PaymentFilterFactory {
    public static Specification<Payment> fromFilter(PaymentFilterCriteria filter) {
        return (root, query, criteriaBuilder) -> {
            final var predicates = new ArrayList<Predicate>();

            if (StringUtils.hasText(filter.currency())) {
                predicates.add(criteriaBuilder.equal(root.get(Payment_.CURRENCY), filter.currency()));
            }

            if (filter.minAmount() != null && filter.maxAmount() != null) {
                predicates.add(criteriaBuilder.between(root.get(Payment_.AMOUNT), filter.minAmount(),
                    filter.maxAmount()));
            }

            if (filter.createdAfter() != null && filter.createdBefore() != null) {
                predicates.add(criteriaBuilder.between(root.get(Payment_.CREATED_AT), filter.createdAfter(),
                    filter.createdBefore()));
            }

            if (filter.status() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Payment_.STATUS), filter.status()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Sort getSort(PaymentFilterCriteria filter) {
        final var sortBy = filter.sortBy();
        if (!StringUtils.hasText(sortBy)) {
            return Sort.unsorted();
        }
        var direction = Sort.Direction.ASC;

        final var sortDirection = filter.sortDirection();
        if (StringUtils.hasText(sortDirection) && sortDirection.equalsIgnoreCase(Sort.Direction.DESC.name())) {
            direction = Sort.Direction.DESC;
        }

        if (sortBy.equalsIgnoreCase(Payment_.AMOUNT) || sortBy.equalsIgnoreCase(Payment_.CREATED_AT)) {
            return Sort.by(direction, sortBy);
        }

        return Sort.unsorted();
    }
}
