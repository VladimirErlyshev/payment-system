package ru.verlyshev.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.verlyshev.model.Payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    final static Map<Long, Payment> payments = Map.of(
        1L, new Payment(1, 100),
        2L, new Payment(2, 12),
        3L, new Payment(3, 340),
        4L, new Payment(4, 500)
    );

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        return Optional.ofNullable(payments.get(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());8
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments.values());
    }
}
