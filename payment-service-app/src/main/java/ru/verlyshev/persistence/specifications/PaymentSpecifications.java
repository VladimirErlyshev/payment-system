package ru.verlyshev.persistence.specifications;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.verlyshev.persistence.entity.Payment;
import ru.verlyshev.persistence.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentSpecifications {

    public static Specification<Payment> hasCurrency(String currency) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("currency"), currency);
    }

    public static Specification<Payment> hasStatus(PaymentStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Payment> amountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("amount"), min, max);
    }

    public static Specification<Payment> createdBetween(OffsetDateTime after, OffsetDateTime before) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("createdAt"), after, before);
    }
}
