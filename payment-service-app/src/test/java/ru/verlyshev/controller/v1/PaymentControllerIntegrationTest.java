package ru.verlyshev.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.verlyshev.configuration.AbstractIntegrationTest;
import ru.verlyshev.configuration.TestJwtFactory;
import ru.verlyshev.dto.request.UpdatePaymentNoteRequest;
import ru.verlyshev.fixtures.JsonParamField;
import ru.verlyshev.fixtures.JsonPathField;
import ru.verlyshev.fixtures.TestFixtures;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.verlyshev.fixtures.TestFixtures.EXISTING_GUID;

@AutoConfigureMockMvc
class PaymentControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String API_V1_PATTERN = "/api/v1/payments";
    private static final String TEST_USER = "test-user";
    private static final String ADMIN_ROLE = "ADMIN";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Sql(scripts = "/data/sql/payments-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data/sql/cleanup-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getByIdTest() throws Exception {
        mockMvc.perform(get("%s/%s".formatted(API_V1_PATTERN, EXISTING_GUID))
                        .with(TestJwtFactory.jwtWithRole(TEST_USER, ADMIN_ROLE))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JsonPathField.GUID).value(EXISTING_GUID));
    }

    @Test
    void createPaymentTest() throws Exception {
        var request = TestFixtures.generatePaymentRequest();

        mockMvc.perform(post(API_V1_PATTERN)
                        .with(TestJwtFactory.jwtWithRole(TEST_USER, ADMIN_ROLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpectAll(TestFixtures.paymentResponseMatchers(request));
    }

    @Test
    @Sql(scripts = "/data/sql/payments-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data/sql/cleanup-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updatePaymentTest() throws Exception {
        var request = TestFixtures.generatePaymentRequest();

        mockMvc.perform(put("%s/%s".formatted(API_V1_PATTERN, EXISTING_GUID))
                        .with(TestJwtFactory.jwtWithRole(TEST_USER, ADMIN_ROLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpectAll(TestFixtures.paymentResponseMatchers(request, UUID.fromString(EXISTING_GUID)));
    }

    @Test
    @Sql(scripts = "/data/sql/payments-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data/sql/cleanup-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updatePaymentNoteTest() throws Exception {
        var newNote = "updatedNote";
        var request = new UpdatePaymentNoteRequest(newNote);
        mockMvc.perform(MockMvcRequestBuilders.patch("%s/%s/note".formatted(API_V1_PATTERN, EXISTING_GUID))
                        .with(TestJwtFactory.jwtWithRole(TEST_USER, ADMIN_ROLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JsonPathField.GUID).value(EXISTING_GUID))
                .andExpect(jsonPath(JsonPathField.NOTE).value(newNote));
    }

    @Test
    @Sql(scripts = "/data/sql/payments-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data/sql/cleanup-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void searchPaymentsTest() throws Exception {
        var existingCurrency = "USD";
        var existingStatus = "APPROVED";
        var minAmount = new BigDecimal("50.00");
        var maxAmount = new BigDecimal("200.00");
        var createdAfter = "2025-01-01T00:00:00+00:00";
        var createdBefore = "2025-01-31T23:59:59+00:00";
        var sortBy = "createdAt";
        var direction = "asc";
        var page = "0";
        var size = "10";

        mockMvc.perform(get("%s/search".formatted(API_V1_PATTERN))
                        .with(TestJwtFactory.jwtWithRole(TEST_USER, ADMIN_ROLE))
                        .param(JsonParamField.CURRENCY, existingCurrency)
                        .param(JsonParamField.STATUS, existingStatus)
                        .param(JsonParamField.MIN_AMOUNT, minAmount.toString())
                        .param(JsonParamField.MAX_AMOUNT, maxAmount.toString())
                        .param(JsonParamField.CREATED_AFTER, createdAfter)
                        .param(JsonParamField.CREATED_BEFORE, createdBefore)
                        .param(JsonParamField.SORT_BY, sortBy)
                        .param(JsonParamField.SORT_DIRECTION, direction)
                        .param(JsonParamField.PAGE, page)
                        .param(JsonParamField.SIZE, size)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JsonPathField.PAGE_NUMBER).value(0))
                .andExpect(jsonPath(JsonPathField.PAGE_SIZE).value(10))
                .andExpect(jsonPath(JsonPathField.FIRST).value(true));
    }
}