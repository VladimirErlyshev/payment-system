package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.verlyshev.dto.PaymentDto;
import ru.verlyshev.dto.PaymentFilterDto;
import ru.verlyshev.mapper.PaymentFilterPersistenceMapper;
import ru.verlyshev.mapper.PaymentPersistenceMapper;
import ru.verlyshev.persistence.entity.Payment;
import ru.verlyshev.persistence.entity.PaymentStatus;
import ru.verlyshev.persistence.repository.PaymentRepository;
import ru.verlyshev.persistence.specifications.PaymentFilterCriteria;
import ru.verlyshev.service.PaymentServiceImpl;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentPersistenceMapper paymentPersistenceMapper;

    @Mock
    private PaymentFilterPersistenceMapper paymentFilterPersistenceMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Nested
    @DisplayName("Search by ID Tests")
    class SearchByIdTests {

        @Test
        @DisplayName("Should find payment by ID successfully")
        void shouldFindPaymentById() {
            // Given
            UUID paymentId = UUID.randomUUID();
            Payment payment = createPayment(paymentId, new BigDecimal("100.00"), "USD");
            PaymentDto paymentDto = createPaymentDto(paymentId, new BigDecimal("100.00"), "USD");

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .guid(paymentId)
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();
            Specification<Payment> spec = mock(Specification.class);

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(List.of(payment));
            when(paymentPersistenceMapper.fromPaymentEntity(payment)).thenReturn(paymentDto);

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).guid()).isEqualTo(paymentId);
            verify(paymentRepository).findAll(any(Specification.class), any(Sort.class));
        }

        @Test
        @DisplayName("Should return empty list when payment not found by ID")
        void shouldReturnEmptyListWhenPaymentNotFound() {
            // Given
            UUID paymentId = UUID.randomUUID();
            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .guid(paymentId)
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(List.of());

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Filter by Criteria Tests")
    class FilterByCriteriaTests {

        @ParameterizedTest
        @ValueSource(strings = {"USD", "EUR", "RUB", "GBP"})
        @DisplayName("Should filter payments by currency")
        void shouldFilterPaymentsByCurrency(String currency) {
            // Given
            Payment payment = createPayment(UUID.randomUUID(), new BigDecimal("100.00"), currency);
            PaymentDto paymentDto = createPaymentDto(payment.getGuid(), new BigDecimal("100.00"), currency);

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .currency(currency)
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(List.of(payment));
            when(paymentPersistenceMapper.fromPaymentEntity(payment)).thenReturn(paymentDto);

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCurrency()).isEqualTo(currency);
        }

        @ParameterizedTest
        @CsvSource({
                "50.00, 1",
                "100.00, 2",
                "200.00, 3"
        })
        @DisplayName("Should filter payments by minimum amount")
        void shouldFilterPaymentsByMinAmount(BigDecimal minAmount, int expectedCount) {
            // Given
            List<Payment> payments = List.of(
                    createPayment(UUID.randomUUID(), new BigDecimal("75.00"), "USD"),
                    createPayment(UUID.randomUUID(), new BigDecimal("150.00"), "USD"),
                    createPayment(UUID.randomUUID(), new BigDecimal("250.00"), "USD")
            );

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .minAmount(minAmount)
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(payments.stream()
                            .filter(p -> p.getAmount().compareTo(minAmount) >= 0)
                            .toList());

            payments.forEach(p ->
                    when(paymentPersistenceMapper.fromPaymentEntity(p))
                            .thenReturn(createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency()))
            );

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(expectedCount);
            assertThat(result).allMatch(dto -> dto.getAmount().compareTo(minAmount) >= 0);
        }

        @ParameterizedTest
        @CsvSource({
                "100.00, 1",
                "200.00, 2",
                "300.00, 3"
        })
        @DisplayName("Should filter payments by maximum amount")
        void shouldFilterPaymentsByMaxAmount(BigDecimal maxAmount, int expectedCount) {
            // Given
            List<Payment> payments = List.of(
                    createPayment(UUID.randomUUID(), new BigDecimal("75.00"), "USD"),
                    createPayment(UUID.randomUUID(), new BigDecimal("150.00"), "USD"),
                    createPayment(UUID.randomUUID(), new BigDecimal("250.00"), "USD")
            );

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .maxAmount(maxAmount)
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(payments.stream()
                            .filter(p -> p.getAmount().compareTo(maxAmount) <= 0)
                            .toList());

            payments.forEach(p ->
                    when(paymentPersistenceMapper.fromPaymentEntity(p))
                            .thenReturn(createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency()))
            );

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(expectedCount);
            assertThat(result).allMatch(dto -> dto.getAmount().compareTo(maxAmount) <= 0);
        }

        @Test
        @DisplayName("Should filter payments by amount range")
        void shouldFilterPaymentsByAmountRange() {
            // Given
            BigDecimal minAmount = new BigDecimal("100.00");
            BigDecimal maxAmount = new BigDecimal("200.00");

            List<Payment> payments = List.of(
                    createPayment(UUID.randomUUID(), new BigDecimal("50.00"), "USD"),
                    createPayment(UUID.randomUUID(), new BigDecimal("150.00"), "USD"),
                    createPayment(UUID.randomUUID(), new BigDecimal("180.00"), "USD"),
                    createPayment(UUID.randomUUID(), new BigDecimal("250.00"), "USD")
            );

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .minAmount(minAmount)
                    .maxAmount(maxAmount)
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(payments.stream()
                            .filter(p -> p.getAmount().compareTo(minAmount) >= 0
                                    && p.getAmount().compareTo(maxAmount) <= 0)
                            .toList());

            payments.forEach(p ->
                    when(paymentPersistenceMapper.fromPaymentEntity(p))
                            .thenReturn(createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency()))
            );

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(dto ->
                    dto.getAmount().compareTo(minAmount) >= 0
                            && dto.getAmount().compareTo(maxAmount) <= 0
            );
        }

        @Test
        @DisplayName("Should filter payments created before specific date")
        void shouldFilterPaymentsCreatedBefore() {
            // Given
            OffsetDateTime cutoffDate = OffsetDateTime.now();
            OffsetDateTime beforeDate = cutoffDate.minusDays(5);

            Payment payment = createPayment(UUID.randomUUID(), new BigDecimal("100.00"), "USD");
            payment.setCreatedAt(beforeDate);
            PaymentDto paymentDto = createPaymentDto(payment.getGuid(), new BigDecimal("100.00"), "USD");
            paymentDto.setCreatedAt(beforeDate);

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .createdBefore(cutoffDate)
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(List.of(payment));
            when(paymentPersistenceMapper.fromPaymentEntity(payment)).thenReturn(paymentDto);

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCreatedAt()).isBefore(cutoffDate);
        }

        @Test
        @DisplayName("Should filter payments created after specific date")
        void shouldFilterPaymentsCreatedAfter() {
            // Given
            OffsetDateTime cutoffDate = OffsetDateTime.now().minusDays(10);
            OffsetDateTime afterDate = cutoffDate.plusDays(5);

            Payment payment = createPayment(UUID.randomUUID(), new BigDecimal("100.00"), "USD");
            payment.setCreatedAt(afterDate);
            PaymentDto paymentDto = createPaymentDto(payment.getGuid(), new BigDecimal("100.00"), "USD");
            paymentDto.setCreatedAt(afterDate);

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .createdAfter(cutoffDate)
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(List.of(payment));
            when(paymentPersistenceMapper.fromPaymentEntity(payment)).thenReturn(paymentDto);

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCreatedAt()).isAfter(cutoffDate);
        }

        @Test
        @DisplayName("Should filter payments by creation date range")
        void shouldFilterPaymentsByDateRange() {
            // Given
            OffsetDateTime startDate = OffsetDateTime.now().minusDays(10);
            OffsetDateTime endDate = OffsetDateTime.now().minusDays(2);

            Payment payment1 = createPayment(UUID.randomUUID(), new BigDecimal("100.00"), "USD");
            payment1.setCreatedAt(startDate.plusDays(3));

            Payment payment2 = createPayment(UUID.randomUUID(), new BigDecimal("150.00"), "USD");
            payment2.setCreatedAt(startDate.plusDays(6));

            PaymentDto dto1 = createPaymentDto(payment1.getGuid(), new BigDecimal("100.00"), "USD");
            dto1.setCreatedAt(payment1.getCreatedAt());

            PaymentDto dto2 = createPaymentDto(payment2.getGuid(), new BigDecimal("150.00"), "USD");
            dto2.setCreatedAt(payment2.getCreatedAt());

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .createdAfter(startDate)
                    .createdBefore(endDate)
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(List.of(payment1, payment2));
            when(paymentPersistenceMapper.fromPaymentEntity(payment1)).thenReturn(dto1);
            when(paymentPersistenceMapper.fromPaymentEntity(payment2)).thenReturn(dto2);

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(dto ->
                    dto.getCreatedAt().isAfter(startDate) && dto.getCreatedAt().isBefore(endDate)
            );
        }

        @ParameterizedTest
        @MethodSource("ru.verlyshev.persistence.service.PaymentServiceTest#providePaymentStatuses")
        @DisplayName("Should filter payments by status")
        void shouldFilterPaymentsByStatus(PaymentStatus status, int expectedCount) {
            // Given
            List<Payment> allPayments = List.of(
                    createPaymentWithStatus(PaymentStatus.PENDING),
                    createPaymentWithStatus(PaymentStatus.COMPLETED),
                    createPaymentWithStatus(PaymentStatus.FAILED)
            );

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .status(status)
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(allPayments.stream()
                            .filter(p -> p.getStatus() == status)
                            .toList());

            allPayments.forEach(p -> {
                PaymentDto dto = createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency());
                dto.setStatus(p.getStatus());
                when(paymentPersistenceMapper.fromPaymentEntity(p)).thenReturn(dto);
            });

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(expectedCount);
            assertThat(result).allMatch(dto -> dto.getStatus() == status);
        }

        @Test
        @DisplayName("Should filter payments by multiple criteria")
        void shouldFilterPaymentsByMultipleCriteria() {
            // Given
            BigDecimal minAmount = new BigDecimal("100.00");
            BigDecimal maxAmount = new BigDecimal("200.00");
            String currency = "USD";
            PaymentStatus status = PaymentStatus.COMPLETED;

            Payment payment = createPayment(UUID.randomUUID(), new BigDecimal("150.00"), currency);
            payment.setStatus(status);

            PaymentDto paymentDto = createPaymentDto(payment.getGuid(), new BigDecimal("150.00"), currency);
            paymentDto.setStatus(status);

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .minAmount(minAmount)
                    .maxAmount(maxAmount)
                    .currency(currency)
                    .status(status)
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(List.of(payment));
            when(paymentPersistenceMapper.fromPaymentEntity(payment)).thenReturn(paymentDto);

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(1);
            assertAll(
                    () -> assertThat(result.get(0).getAmount()).isBetween(minAmount, maxAmount),
                    () -> assertThat(result.get(0).getCurrency()).isEqualTo(currency),
                    () -> assertThat(result.get(0).getStatus()).isEqualTo(status)
            );
        }
    }

    @Nested
    @DisplayName("Sorting Tests")
    class SortingTests {

        @ParameterizedTest
        @MethodSource("ru.verlyshev.persistence.service.PaymentServiceTest#provideSortParameters")
        @DisplayName("Should sort payments by amount")
        void shouldSortPaymentsByAmount(Sort.Direction direction) {
            // Given
            List<Payment> payments = List.of(
                    createPayment(UUID.randomUUID(), new BigDecimal("150.00"), "USD"),
                    createPayment(UUID.randomUUID(), new BigDecimal("50.00"), "USD"),
                    createPayment(UUID.randomUUID(), new BigDecimal("100.00"), "USD")
            );

            List<Payment> sortedPayments = direction == Sort.Direction.ASC
                    ? List.of(payments.get(1), payments.get(2), payments.get(0))
                    : List.of(payments.get(0), payments.get(2), payments.get(1));

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .sortBy("amount")
                    .sortDirection(direction.name())
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(sortedPayments);

            sortedPayments.forEach(p ->
                    when(paymentPersistenceMapper.fromPaymentEntity(p))
                            .thenReturn(createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency()))
            );

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(3);
            if (direction == Sort.Direction.ASC) {
                assertThat(result.get(0).getAmount()).isLessThan(result.get(1).getAmount());
                assertThat(result.get(1).getAmount()).isLessThan(result.get(2).getAmount());
            } else {
                assertThat(result.get(0).getAmount()).isGreaterThan(result.get(1).getAmount());
                assertThat(result.get(1).getAmount()).isGreaterThan(result.get(2).getAmount());
            }
        }

        @ParameterizedTest
        @MethodSource("ru.verlyshev.persistence.service.PaymentServiceTest#provideSortParameters")
        @DisplayName("Should sort payments by creation date")
        void shouldSortPaymentsByCreatedAt(Sort.Direction direction) {
            // Given
            OffsetDateTime now = OffsetDateTime.now();

            Payment payment1 = createPayment(UUID.randomUUID(), new BigDecimal("100.00"), "USD");
            payment1.setCreatedAt(now.minusDays(3));

            Payment payment2 = createPayment(UUID.randomUUID(), new BigDecimal("100.00"), "USD");
            payment2.setCreatedAt(now.minusDays(1));

            Payment payment3 = createPayment(UUID.randomUUID(), new BigDecimal("100.00"), "USD");
            payment3.setCreatedAt(now.minusDays(2));

            List<Payment> sortedPayments = direction == Sort.Direction.ASC
                    ? List.of(payment1, payment3, payment2)
                    : List.of(payment2, payment3, payment1);

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .sortBy("createdAt")
                    .sortDirection(direction.name())
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(sortedPayments);

            sortedPayments.forEach(p -> {
                PaymentDto dto = createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency());
                dto.setCreatedAt(p.getCreatedAt());
                when(paymentPersistenceMapper.fromPaymentEntity(p)).thenReturn(dto);
            });

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(3);
            if (direction == Sort.Direction.ASC) {
                assertThat(result.get(0).getCreatedAt()).isBefore(result.get(1).getCreatedAt());
                assertThat(result.get(1).getCreatedAt()).isBefore(result.get(2).getCreatedAt());
            } else {
                assertThat(result.get(0).getCreatedAt()).isAfter(result.get(1).getCreatedAt());
                assertThat(result.get(1).getCreatedAt()).isAfter(result.get(2).getCreatedAt());
            }
        }
    }

    @Nested
    @DisplayName("Pagination Tests")
    class PaginationTests {

        @Test
        @DisplayName("Should return first page with default size (25 elements)")
        void shouldReturnFirstPageWithDefaultSize() {
            // Given
            Pageable pageable = PageRequest.of(0, 25);
            List<Payment> payments = createPaymentList(25);
            Page<Payment> paymentsPage = new PageImpl<>(payments, pageable, 100);

            PaymentFilterDto filterDto = PaymentFilterDto.builder().build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(paymentsPage);

            payments.forEach(p ->
                    when(paymentPersistenceMapper.fromPaymentEntity(p))
                            .thenReturn(createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency()))
            );

            // When
            Page<PaymentDto> result = paymentService.searchPaged(filterDto, pageable);

            // Then
            assertAll(
                    () -> assertThat(result.getContent()).hasSize(25),
                    () -> assertThat(result.getNumber()).isEqualTo(0),
                    () -> assertThat(result.getSize()).isEqualTo(25),
                    () -> assertThat(result.getTotalElements()).isEqualTo(100),
                    () -> assertThat(result.getTotalPages()).isEqualTo(4)
            );
        }

        @ParameterizedTest
        @CsvSource({
                "0, 10",
                "1, 10",
                "2, 10",
                "0, 25",
                "1, 25"
        })
        @DisplayName("Should return specific page with custom size")
        void shouldReturnSpecificPageWithCustomSize(int pageNumber, int pageSize) {
            // Given
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<Payment> payments = createPaymentList(pageSize);
            Page<Payment> paymentsPage = new PageImpl<>(payments, pageable, 100);

            PaymentFilterDto filterDto = PaymentFilterDto.builder().build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(paymentsPage);

            payments.forEach(p ->
                    when(paymentPersistenceMapper.fromPaymentEntity(p))
                            .thenReturn(createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency()))
            );

            // When
            Page<PaymentDto> result = paymentService.searchPaged(filterDto, pageable);

            // Then
            assertAll(
                    () -> assertThat(result.getContent()).hasSize(pageSize),
                    () -> assertThat(result.getNumber()).isEqualTo(pageNumber),
                    () -> assertThat(result.getSize()).isEqualTo(pageSize)
            );
        }

        @Test
        @DisplayName("Should return empty page when no results")
        void shouldReturnEmptyPageWhenNoResults() {
            // Given
            Pageable pageable = PageRequest.of(0, 25);
            Page<Payment> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            PaymentFilterDto filterDto = PaymentFilterDto.builder().build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(emptyPage);

            // When
            Page<PaymentDto> result = paymentService.searchPaged(filterDto, pageable);

            // Then
            assertAll(
                    () -> assertThat(result.getContent()).isEmpty(),
                    () -> assertThat(result.getTotalElements()).isEqualTo(0),
                    () -> assertThat(result.getTotalPages()).isEqualTo(0)
            );
        }

        @Test
        @DisplayName("Should combine pagination with filtering")
        void shouldCombinePaginationWithFiltering() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            String currency = "USD";

            List<Payment> payments = createPaymentList(10, currency);
            Page<Payment> paymentsPage = new PageImpl<>(payments, pageable, 50);

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .currency(currency)
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(paymentsPage);

            payments.forEach(p ->
                    when(paymentPersistenceMapper.fromPaymentEntity(p))
                            .thenReturn(createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency()))
            );

            // When
            Page<PaymentDto> result = paymentService.searchPaged(filterDto, pageable);

            // Then
            assertAll(
                    () -> assertThat(result.getContent()).hasSize(10),
                    () -> assertThat(result.getContent()).allMatch(dto -> dto.getCurrency().equals(currency)),
                    () -> assertThat(result.getTotalElements()).isEqualTo(50)
            );
        }

        @Test
        @DisplayName("Should combine pagination with sorting")
        void shouldCombinePaginationWithSorting() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Sort sort = Sort.by(Sort.Direction.DESC, "amount");

            List<Payment> payments = createPaymentList(10);
            Page<Payment> paymentsPage = new PageImpl<>(payments, PageRequest.of(0, 10, sort), 50);

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .sortBy("amount")
                    .sortDirection("DESC")
                    .build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(paymentsPage);

            payments.forEach(p ->
                    when(paymentPersistenceMapper.fromPaymentEntity(p))
                            .thenReturn(createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency()))
            );

            // When
            Page<PaymentDto> result = paymentService.searchPaged(filterDto, pageable);

            // Then
            assertAll(
                    () -> assertThat(result.getContent()).hasSize(10),
                    () -> assertThat(result.getNumber()).isEqualTo(0),
                    () -> assertThat(result.getSize()).isEqualTo(10)
            );
            verify(paymentRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @ParameterizedTest
        @CsvSource({
                "0, 5, 20",
                "1, 5, 20",
                "3, 5, 20",
                "0, 10, 25",
                "2, 10, 25"
        })
        @DisplayName("Should handle pagination with different page numbers")
        void shouldHandlePaginationWithDifferentPageNumbers(int pageNumber, int pageSize, int totalElements) {
            // Given
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<Payment> payments = createPaymentList(Math.min(pageSize, totalElements - pageNumber * pageSize));
            Page<Payment> paymentsPage = new PageImpl<>(payments, pageable, totalElements);

            PaymentFilterDto filterDto = PaymentFilterDto.builder().build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(paymentsPage);

            payments.forEach(p ->
                    when(paymentPersistenceMapper.fromPaymentEntity(p))
                            .thenReturn(createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency()))
            );

            // When
            Page<PaymentDto> result = paymentService.searchPaged(filterDto, pageable);

            // Then
            assertAll(
                    () -> assertThat(result.getNumber()).isEqualTo(pageNumber),
                    () -> assertThat(result.getSize()).isEqualTo(pageSize),
                    () -> assertThat(result.getTotalElements()).isEqualTo(totalElements)
            );
        }
    }

    @Nested
    @DisplayName("Complex Scenarios Tests")
    class ComplexScenariosTests {

        @Test
        @DisplayName("Should handle complex filter with pagination and sorting")
        void shouldHandleComplexFilterWithPaginationAndSorting() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");

            PaymentFilterDto filterDto = PaymentFilterDto.builder()
                    .currency("USD")
                    .minAmount(new BigDecimal("50.00"))
                    .maxAmount(new BigDecimal("200.00"))
                    .status(PaymentStatus.COMPLETED)
                    .sortBy("createdAt")
                    .sortDirection("ASC")
                    .build();

            List<Payment> payments = createPaymentList(10, "USD");
            payments.forEach(p -> p.setStatus(PaymentStatus.COMPLETED));

            Page<Payment> paymentsPage = new PageImpl<>(payments, PageRequest.of(0, 10, sort), 30);

            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(paymentsPage);

            payments.forEach(p -> {
                PaymentDto dto = createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency());
                dto.setStatus(p.getStatus());
                when(paymentPersistenceMapper.fromPaymentEntity(p)).thenReturn(dto);
            });

            // When
            Page<PaymentDto> result = paymentService.searchPaged(filterDto, pageable);

            // Then
            assertAll(
                    () -> assertThat(result.getContent()).hasSize(10),
                    () -> assertThat(result.getContent()).allMatch(dto ->
                            dto.getCurrency().equals("USD") && dto.getStatus() == PaymentStatus.COMPLETED
                    ),
                    () -> assertThat(result.getTotalElements()).isEqualTo(30)
            );
        }

        @Test
        @DisplayName("Should handle empty filter")
        void shouldHandleEmptyFilter() {
            // Given
            PaymentFilterDto filterDto = PaymentFilterDto.builder().build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            List<Payment> payments = createPaymentList(5);

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(payments);

            payments.forEach(p ->
                    when(paymentPersistenceMapper.fromPaymentEntity(p))
                            .thenReturn(createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency()))
            );

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(5);
            verify(paymentRepository).findAll(any(Specification.class), any(Sort.class));
        }

        @Test
        @DisplayName("Should verify mapper is called for each payment")
        void shouldVerifyMapperIsCalledForEachPayment() {
            // Given
            List<Payment> payments = createPaymentList(3);
            PaymentFilterDto filterDto = PaymentFilterDto.builder().build();
            PaymentFilterCriteria criteria = new PaymentFilterCriteria();

            when(paymentFilterPersistenceMapper.toFilterCriteria(filterDto)).thenReturn(criteria);
            when(paymentRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(payments);

            payments.forEach(p ->
                    when(paymentPersistenceMapper.fromPaymentEntity(p))
                            .thenReturn(createPaymentDto(p.getGuid(), p.getAmount(), p.getCurrency()))
            );

            // When
            List<PaymentDto> result = paymentService.search(filterDto);

            // Then
            assertThat(result).hasSize(3);
            verify(paymentPersistenceMapper, times(3)).fromPaymentEntity(any(Payment.class));
        }
    }

    // Helper methods
    private Payment createPayment(UUID guid, BigDecimal amount, String currency) {
        return Payment.builder()
                .guid(guid)
                .inquiryRefId(UUID.randomUUID())
                .amount(amount)
                .currency(currency)
                .transactionRefId(UUID.randomUUID())
                .status(PaymentStatus.PENDING)
                .note("Test payment")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    private Payment createPaymentWithStatus(PaymentStatus status) {
        Payment payment = createPayment(UUID.randomUUID(), new BigDecimal("100.00"), "USD");
        payment.setStatus(status);
        return payment;
    }

    private PaymentDto createPaymentDto(UUID guid, BigDecimal amount, String currency) {
        return PaymentDto.builder()
                .guid(guid)
                .inquiryRefId(UUID.randomUUID())
                .amount(amount)
                .currency(currency)
                .transactionRefId(UUID.randomUUID())
                .status(PaymentStatus.PENDING)
                .note("Test payment")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    private List<Payment> createPaymentList(int count) {
        return Stream.generate(() -> createPayment(
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                "USD"
        )).limit(count).toList();
    }

    private List<Payment> createPaymentList(int count, String currency) {
        return Stream.generate(() -> createPayment(
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                currency
        )).limit(count).toList();
    }

    // Method sources for parameterized tests
    static Stream<Arguments> providePaymentStatuses() {
        return Stream.of(
                Arguments.of(PaymentStatus.PENDING, 1),
                Arguments.of(PaymentStatus.COMPLETED, 1),
                Arguments.of(PaymentStatus.FAILED, 1)
        );
    }

    static Stream<Sort.Direction> provideSortParameters() {
        return Stream.of(Sort.Direction.ASC, Sort.Direction.DESC);
    }
}
