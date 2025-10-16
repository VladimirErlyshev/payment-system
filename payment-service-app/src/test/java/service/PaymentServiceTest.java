package service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.verlyshev.persistence.specifications.PaymentFilterFactory;
import ru.verlyshev.service.PaymentServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static fixtures.TestFixtures.createDate;
import static fixtures.TestFixtures.currentDate;
import static fixtures.TestFixtures.id;
import static fixtures.TestFixtures.inquiryRefId;
import static fixtures.TestFixtures.transactionId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentFilterPersistenceMapper filterMapper;

    @Mock
    private PaymentPersistenceMapper paymentPersistenceMapper;

    @InjectMocks
    private PaymentServiceImpl service;

    private Payment payment;
    private PaymentDto paymentDto;
    private PaymentFilterDto filterDto;
    private PaymentFilterCriteria criteria;
    private Specification<Payment> spec;
    private Sort sort;

    private MockedStatic<PaymentFilterFactory> mockedPaymentFilterFactory;

    private static Stream<Arguments> pages() {
        return Stream.of(
                Arguments.of(PageRequest.of(0, 10)),
                Arguments.of(PageRequest.of(0, 10, Sort.by("guid")))
        );
    }

    @BeforeEach
    void setUp() {
        mockedPaymentFilterFactory = mockStatic(PaymentFilterFactory.class);

        payment = Payment.builder()
                .guid(id)
                .inquiryRefId(inquiryRefId)
                .amount(new BigDecimal("100.00"))
                .currency("RUB")
                .transactionRefId(transactionId)
                .status(PaymentStatus.PENDING)
                .note("Test payment")
                .createdAt(createDate)
                .updatedAt(currentDate)
                .build();

        paymentDto = new PaymentDto(
                id,
                inquiryRefId,
                new BigDecimal("100.00"),
                "RUB",
                transactionId,
                PaymentStatus.PENDING,
                "Test payment",
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );

        filterDto = new PaymentFilterDto(
                "RUB",
                new BigDecimal("50.00"),
                null,
                null,
                null,
                null,
                "amount",
                "ASC"
        );

        criteria = new PaymentFilterCriteria(
                "RUB",
                new BigDecimal("50.00"),
                null,
                null,
                null,
                null,
                "amount",
                "ASC"
        );

        spec = mock(Specification.class);
        sort = Sort.by(Sort.Direction.ASC, "amount");



        mockedPaymentFilterFactory.when(() -> PaymentFilterFactory.fromFilter(any(PaymentFilterCriteria.class)))
                .thenReturn(spec);

        mockedPaymentFilterFactory.when(() -> PaymentFilterFactory.getSort(any(PaymentFilterCriteria.class)))
                .thenReturn(sort);

        lenient().when(paymentPersistenceMapper.fromPaymentEntity(any(Payment.class)))
                .thenReturn(paymentDto);
    }

    @AfterEach
    void tearDown() {
        if (mockedPaymentFilterFactory != null) {
            mockedPaymentFilterFactory.close();
        }
    }

    @ParameterizedTest
    @MethodSource("pages")
    void searchPaged(Pageable pageable) {
        // Given
        when(paymentRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable actualPageable = invocation.getArgument(1);
                    return new PageImpl<>(List.of(payment), actualPageable, 1);
                });
        when(filterMapper.toFilterCriteria(any(PaymentFilterDto.class)))
                .thenReturn(criteria);

        // When
        var result = service.searchPaged(filterDto, pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(pageable.getPageNumber(), result.getNumber());
        assertEquals(pageable.getPageSize(), result.getSize());
    }

    @Test
    void searchPagedAppliesFilterSort() {
        // Given
        Pageable input = PageRequest.of(0, 10, Sort.by("guid"));
        Pageable expected = PageRequest.of(0, 10, sort);
        when(filterMapper.toFilterCriteria(any(PaymentFilterDto.class)))
                .thenReturn(criteria);
        when(paymentRepository.findAll(spec, expected))
                .thenReturn(new PageImpl<>(List.of(payment), expected, 1));

        // When
        var result = service.searchPaged(filterDto, input);

        // Then
        assertEquals(1, result.getTotalElements());
        verify(paymentRepository).findAll(spec, expected);
    }

    @Test
    void searchPagedKeepsPageableSortIfNoFilterSort() {
        // Given
        var unsorted = Sort.unsorted();
        mockedPaymentFilterFactory.when(() -> PaymentFilterFactory.getSort(any(PaymentFilterCriteria.class)))
                .thenReturn(unsorted);

        var input = PageRequest.of(0, 10, Sort.by("guid"));
        when(paymentRepository.findAll(spec, input))
                .thenReturn(new PageImpl<>(List.of(payment), input, 1));

        when(filterMapper.toFilterCriteria(any(PaymentFilterDto.class)))
                .thenReturn(criteria);

        // When
        var result = service.searchPaged(filterDto, input);

        // Then
        assertEquals(1, result.getTotalElements());
        verify(paymentRepository).findAll(spec, input);
    }

    @Test
    void shouldSaveAndReturnMappedDto() {
        // Given
        PaymentDto inputDto = new PaymentDto(
                null,
                inquiryRefId,
                new BigDecimal("100.00"),
                "RUB",
                transactionId,
                PaymentStatus.PENDING,
                "Test payment",
                null,
                null
        );

        Payment savedEntity = Payment.builder()
                .guid(id)
                .inquiryRefId(inquiryRefId)
                .amount(new BigDecimal("100.00"))
                .currency("RUB")
                .transactionRefId(transactionId)
                .status(PaymentStatus.PENDING)
                .note("Test payment")
                .createdAt(currentDate)
                .updatedAt(currentDate)
                .build();

        PaymentDto expectedDto = new PaymentDto(
                id,
                inquiryRefId,
                new BigDecimal("100.00"),
                "RUB",
                transactionId,
                PaymentStatus.PENDING,
                "Test payment",
                currentDate,
                currentDate
        );

        when(paymentPersistenceMapper.toPaymentEntity(inputDto)).thenReturn(savedEntity);

        when(paymentRepository.save(savedEntity)).thenReturn(savedEntity);

        when(paymentPersistenceMapper.fromPaymentEntity(savedEntity)).thenReturn(expectedDto);

        // When
        PaymentDto result = service.create(inputDto);

        // Then
        assertEquals(expectedDto, result);
        verify(paymentPersistenceMapper).toPaymentEntity(inputDto);
        verify(paymentRepository).save(savedEntity);
        verify(paymentPersistenceMapper).fromPaymentEntity(savedEntity);
    }

    @Test
    void shouldReturnMappedDtoWhenPaymentExists() {
        // Given
        var testGuid = id;

        when(paymentRepository.findById(testGuid)).thenReturn(java.util.Optional.of(payment));

        when(paymentPersistenceMapper.fromPaymentEntity(payment)).thenReturn(paymentDto);

        // When
        PaymentDto result = service.getPaymentById(testGuid);

        // Then
        assertEquals(paymentDto, result);
        verify(paymentRepository).findById(testGuid);
        verify(paymentPersistenceMapper).fromPaymentEntity(payment);
    }

    @Test
    void shouldUpdateExistingPaymentAndPreserveAuditFields() {
        // Given
        UUID paymentId = id;

        PaymentDto updateDto = new PaymentDto(
                null,
                inquiryRefId,
                new BigDecimal("200.00"),
                "USD",
                transactionId,
                PaymentStatus.APPROVED,
                "Updated note",
                null,
                null
        );

        Payment existingEntity = Payment.builder()
                .guid(paymentId)
                .inquiryRefId(inquiryRefId)
                .amount(new BigDecimal("100.00"))
                .currency("RUB")
                .transactionRefId(transactionId)
                .status(PaymentStatus.PENDING)
                .note("Test payment")
                .createdAt(createDate)
                .updatedAt(currentDate)
                .build();

        Payment savedEntity = Payment.builder()
                .guid(paymentId)
                .inquiryRefId(inquiryRefId)
                .amount(new BigDecimal("200.00"))
                .currency("USD")
                .transactionRefId(transactionId)
                .status(PaymentStatus.APPROVED)
                .note("Updated note")
                .createdAt(createDate)
                .updatedAt(currentDate) // или обновляется автоматически
                .build();

        PaymentDto expectedDto = new PaymentDto(
                paymentId,
                inquiryRefId,
                new BigDecimal("200.00"),
                "USD",
                transactionId,
                PaymentStatus.APPROVED,
                "Updated note",
                createDate,
                currentDate
        );

        when(paymentRepository.findByIdWithLock(paymentId)).thenReturn(Optional.of(existingEntity));

        doAnswer(invocation -> {
            PaymentDto dto = invocation.getArgument(0);
            Payment entity = invocation.getArgument(1);
            entity.setAmount(dto.amount());
            entity.setCurrency(dto.currency());
            entity.setTransactionRefId(dto.transactionRefId());
            entity.setStatus(dto.status());
            entity.setNote(dto.note());
            return null;
        }).when(paymentPersistenceMapper).updatePaymentEntityFromDto(eq(updateDto), eq(existingEntity));

        when(paymentRepository.save(existingEntity)).thenReturn(savedEntity);
        when(paymentPersistenceMapper.fromPaymentEntity(savedEntity)).thenReturn(expectedDto);

        // When
        PaymentDto result = service.update(paymentId, updateDto);

        // Then
        assertEquals(expectedDto, result);
        verify(paymentRepository).findByIdWithLock(paymentId);
        verify(paymentPersistenceMapper).updatePaymentEntityFromDto(eq(updateDto), eq(existingEntity));
        verify(paymentRepository).save(same(existingEntity));
        verify(paymentPersistenceMapper).fromPaymentEntity(savedEntity);

        assertEquals("USD", existingEntity.getCurrency());
        assertEquals(new BigDecimal("200.00"), existingEntity.getAmount());
        assertEquals(createDate, existingEntity.getCreatedAt());
    }

    @Test
    void shouldDeletePaymentWhenExists() {
        // Given
        var paymentId = id;

        when(paymentRepository.existsById(paymentId)).thenReturn(true);

        doNothing().when(paymentRepository).deleteById(paymentId);

        // When
        service.delete(paymentId);

        // Then
        verify(paymentRepository).existsById(paymentId);
        verify(paymentRepository).deleteById(paymentId);
    }
}