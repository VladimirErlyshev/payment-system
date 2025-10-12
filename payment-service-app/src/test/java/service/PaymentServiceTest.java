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
import java.util.stream.Stream;

import static fixtures.TestFixtures.createDate;
import static fixtures.TestFixtures.currentDate;
import static fixtures.TestFixtures.id;
import static fixtures.TestFixtures.inquiryRefId;
import static fixtures.TestFixtures.transactionId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    private PaymentPersistenceMapper dtoMapper;

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

        when(filterMapper.toFilterCriteria(any(PaymentFilterDto.class)))
                .thenReturn(criteria);

        mockedPaymentFilterFactory.when(() -> PaymentFilterFactory.fromFilter(any(PaymentFilterCriteria.class)))
                .thenReturn(spec);

        mockedPaymentFilterFactory.when(() -> PaymentFilterFactory.getSort(any(PaymentFilterCriteria.class)))
                .thenReturn(sort);

        when(dtoMapper.fromPaymentEntity(any(Payment.class)))
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
        when(paymentRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable actualPageable = invocation.getArgument(1);
                    return new PageImpl<>(List.of(payment), actualPageable, 1);
                });

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

        // When
        var result = service.searchPaged(filterDto, input);

        // Then
        assertEquals(1, result.getTotalElements());
        verify(paymentRepository).findAll(spec, input);
    }
}