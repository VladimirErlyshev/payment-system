package ru.verlyshev.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.verlyshev.model.PaymentFilter;
import ru.verlyshev.persistence.entity.Payment;
import ru.verlyshev.service.PaymentService;

@RestController
@RequestMapping("api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/search")
    public Page<Payment> searchPayments(
            @ModelAttribute PaymentFilter filter,
            @PageableDefault(size = 25)
            Pageable pageable
    ) {
        return paymentService.searchPaged(filter, pageable);
    }
}
