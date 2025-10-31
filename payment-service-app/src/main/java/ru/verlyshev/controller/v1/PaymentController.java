package ru.verlyshev.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("Getting payment by id: {}", id);
        final var result = paymentService.getPaymentById(id);
        log.debug("Sending payment: {}", result);
        return ResponseEntity.ok(paymentControllerMapper.toResponse(result));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('READER', 'ADMIN')")
    public Page<PaymentResponse> searchPayments(
        @ModelAttribute PaymentFilterRequest filter,
        @PageableDefault(size = 25) Pageable pageable
    ) {
        log.info("Searching payments with filter: {}", filter);
        final var searchCriteria = paymentFilterMapper.toDto(filter);

        final var result = paymentService.searchPaged(searchCriteria, pageable)
                .map(paymentControllerMapper::toResponse);
        log.debug("Sending payments: {}", result);
        return result;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> create(@RequestBody @Valid PaymentRequest request) {
        log.info("Creating payment: {}", request);
        final var paymentDto = paymentControllerMapper.fromRequest(request);

        final var savedDto = paymentService.create(paymentDto);
        final var response = paymentControllerMapper.toResponse(savedDto);
        log.debug("Created payment: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> update(@PathVariable UUID id, @RequestBody @Valid PaymentRequest request) {
        log.info("Updating payment with id: {} and data: {}", id, request);
        final var dtoToUpdate = paymentControllerMapper.fromRequest(request);

        final var updatedDto = paymentService.update(id, dtoToUpdate);
        final var response = paymentControllerMapper.toResponse(updatedDto);

        log.debug("Updated payment: {}", response);
        return ResponseEntity.ok(response);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        log.info("Deleting payment with id: {}", id);
        paymentService.delete(id);
        log.debug("Deleted payment with id: {}", id);
    }

    @PatchMapping("/{id}/note")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> updateNote(@PathVariable UUID id,
        @RequestBody UpdatePaymentNoteRequest request) {
        log.info("Updating note for payment with id: {} and note: {}", id, request.note());
        final var updatedDto = paymentService.updateNote(id, request.note());
        final var response = paymentControllerMapper.toResponse(updatedDto);

        log.debug("Updated note for payment: {}", response);
        return ResponseEntity.ok(response);
    }
}
