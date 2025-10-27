package ru.verlyshev.controller.v1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.verlyshev.configuration.AbstractIntegrationTest;
import ru.verlyshev.configuration.TestJwtFactory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class PaymentControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String API_V1_PATTERN = "/api/v1/payments/";
    private static final String TEST_USER = "test-user";
    private static final String ADMIN_ROLE = "ADMIN";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(scripts = "/data/sql/payments-test-data.sql")
    void testGetById() throws Exception {
        var uuid = "a668f828-c2c5-4b83-8c41-ddd8b3ac3781";

        mockMvc.perform(get(API_V1_PATTERN + uuid)
                        .with(TestJwtFactory.jwtWithRole(TEST_USER, ADMIN_ROLE))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value(uuid));
    }
}