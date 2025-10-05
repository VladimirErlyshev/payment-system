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
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.dto.PaymentFilterDto;
import ru.verlyshev.mapper.PaymentFilterMapper;
import ru.verlyshev.persistence.specifications.PaymentFilterFactory;
import ru.verlyshev.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentFilterMapper paymentFilterMapper;

    @GetMapping("/search")
    public Page<PaymentDto> searchPayments(
        @ModelAttribute PaymentFilterDto filter,
        @PageableDefault(size = 25) Pageable pageable
    ) {
        final var sort = PaymentFilterFactory.getSort(paymentFilterMapper.toEntityFilter(filter));
        if (sort.isSorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    sort
            );
        }

        return paymentService.searchPaged(filter, pageable);
    }
}
