package ru.verlyshev.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.verlyshev.dto.PaymentDto;
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
        final var result = paymentService.getPaymentById(id);
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PaymentResponse> create(@RequestBody @Valid PaymentRequest request) {
        final var paymentDto = new PaymentDto(
            null,
            request.inquiryRefId(),
            request.amount(),
            request.currency(),
            request.transactionRefId(),
            request.status(),
            request.note(),
            null,
            null
        );

        final var savedDto = paymentService.create(paymentDto);
        final var response = paymentControllerMapper.toResponse(savedDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> update(@PathVariable UUID id, @RequestBody @Valid PaymentRequest request) {
        final var existingDto = paymentService.getPaymentById(id);
        final var dtoToUpdate = new PaymentDto(
            existingDto.guid(),
            request.inquiryRefId(),
            request.amount(),
            request.currency(),
            request.transactionRefId(),
            request.status(),
            request.note(),
            existingDto.createdAt(),
            null
        );

        final var updatedDto = paymentService.update(id, dtoToUpdate);
        final var response = paymentControllerMapper.toResponse(updatedDto);

        return ResponseEntity.ok(response);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        paymentService.delete(id);
    }

    @PatchMapping("/{id}/note")
    public ResponseEntity<PaymentResponse> updateNote(@PathVariable UUID id, @RequestBody PaymentRequest request) {
        final var updatedDto = paymentService.updateNote(id, request.note());
        final var response = paymentControllerMapper.toResponse(updatedDto);

        return ResponseEntity.ok(response);
    }
}
