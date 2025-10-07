package ru.verlyshev.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.verlyshev.response.PaymentResponse;
import ru.verlyshev.request.PaymentFilterRequest;
import ru.verlyshev.mapper.PaymentFilterControllerMapper;
import ru.verlyshev.persistence.specifications.PaymentFilterFactory;
import ru.verlyshev.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentFilterControllerMapper paymentFilterMapper;

    @GetMapping("/search")
    public Page<PaymentResponse> searchPayments(
        @ModelAttribute PaymentFilterRequest filter,
        @PageableDefault(size = 25) Pageable pageable
    ) {
        final var searchCriteria = paymentFilterMapper.toDto(filter);
        return paymentService.searchPaged(searchCriteria, pageable);
    }
}
