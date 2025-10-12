package ru.verlyshev.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.verlyshev.dto.request.PaymentFilterRequest;
import ru.verlyshev.dto.request.PaymentRequest;
import ru.verlyshev.dto.response.PaymentResponse;
import ru.verlyshev.mapper.PaymentControllerMapper;
import ru.verlyshev.mapper.PaymentFilterControllerMapper;
import ru.verlyshev.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentFilterControllerMapper paymentFilterMapper;
    private final PaymentControllerMapper paymentControllerMapper;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable UUID id) {
        final var result = paymentService.findById(id);
        return ResponseEntity.ok(paymentControllerMapper.toResponse(result));
    }

    @GetMapping("/search")
    public Page<PaymentResponse> searchPayments(
        @ModelAttribute PaymentFilterRequest filter,
        @PageableDefault(size = 25) Pageable pageable
    ) {
        final var searchCriteria = paymentFilterMapper.toDto(filter);
        return paymentService.searchPaged(searchCriteria, pageable)
                .map(paymentControllerMapper::toResponse);
    }

    @PostMapping
    public PaymentResponse create(@RequestBody PaymentRequest request) {

    }
}
