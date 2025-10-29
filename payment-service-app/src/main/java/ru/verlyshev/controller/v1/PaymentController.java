package ru.verlyshev.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import ru.verlyshev.dto.request.PaymentFilterRequest;
import ru.verlyshev.dto.request.PaymentRequest;
import ru.verlyshev.dto.request.UpdatePaymentNoteRequest;
import ru.verlyshev.dto.response.PaymentResponse;
import ru.verlyshev.mapper.PaymentControllerMapper;
import ru.verlyshev.mapper.PaymentFilterControllerMapper;
import ru.verlyshev.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentFilterControllerMapper paymentFilterMapper;
    private final PaymentControllerMapper paymentControllerMapper;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('READER', 'ADMIN')")
    public ResponseEntity<PaymentResponse> findById(@PathVariable UUID id) {
        final var result = paymentService.getPaymentById(id);
        return ResponseEntity.ok(paymentControllerMapper.toResponse(result));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('READER', 'ADMIN')")
    public Page<PaymentResponse> searchPayments(
        @ModelAttribute PaymentFilterRequest filter,
        @PageableDefault(size = 25) Pageable pageable
    ) {
        final var searchCriteria = paymentFilterMapper.toDto(filter);
        return paymentService.searchPaged(searchCriteria, pageable)
                .map(paymentControllerMapper::toResponse);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> create(@RequestBody @Valid PaymentRequest request) {
        final var paymentDto = paymentControllerMapper.fromRequest(request);

        final var savedDto = paymentService.create(paymentDto);
        final var response = paymentControllerMapper.toResponse(savedDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> update(@PathVariable UUID id, @RequestBody @Valid PaymentRequest request) {
        final var dtoToUpdate = paymentControllerMapper.fromRequest(request);

        final var updatedDto = paymentService.update(id, dtoToUpdate);
        final var response = paymentControllerMapper.toResponse(updatedDto);

        return ResponseEntity.ok(response);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        paymentService.delete(id);
    }

    @PatchMapping("/{id}/note")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> updateNote(@PathVariable UUID id,
        @RequestBody UpdatePaymentNoteRequest request) {
        final var updatedDto = paymentService.updateNote(id, request.note());
        final var response = paymentControllerMapper.toResponse(updatedDto);

        return ResponseEntity.ok(response);
    }
}