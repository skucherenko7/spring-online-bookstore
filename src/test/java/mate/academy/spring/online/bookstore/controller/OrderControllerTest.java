package mate.academy.spring.online.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.spring.online.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.spring.online.bookstore.dto.order.OrderResponseDto;
import mate.academy.spring.online.bookstore.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.spring.online.bookstore.dto.orderitem.OrderItemResponseDto;
import mate.academy.spring.online.bookstore.model.Order.Status;
import mate.academy.spring.online.bookstore.util.OrderUtil;
import mate.academy.spring.online.bookstore.util.UserUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTest {

    private static MockMvc mockMvc;

    private static final String FULL_ORDER_SETUP_SQL = "database/orders/full-order-setup.sql";
    private static final String DELETE_ALL_DATA_SQL = "database/delete-all-data-db.sql";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataSource dataSource;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(FULL_ORDER_SETUP_SQL));
        }
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Place order with valid data")
    void placeOrder_ValidData_ShouldReturnOrderResponseDto() throws Exception {
        CreateOrderRequestDto orderRequestDto = new CreateOrderRequestDto("Some shipping address");
        String jsonRequest = objectMapper.writeValueAsString(orderRequestDto);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                UserUtil.getUser(), null, UserUtil.getUser().getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        MvcResult result = mockMvc.perform(post("/orders")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andReturn();

        OrderResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderResponseDto.class
        );

        assertThat(actual).isNotNull();
        assertThat(actual.userId()).isEqualTo(1L);
        assertThat(actual.status()).isEqualTo("PENDING");
        assertThat(actual.orderItems()).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Retrieve user order history")
    void getUserOrderHistory_ValidData_ShouldReturnOrderList() throws Exception {
        List<OrderResponseDto> expected = OrderUtil.getListResponseDto();

        UsernamePasswordAuthenticationToken auth = new
                UsernamePasswordAuthenticationToken(UserUtil.getUser(), null,
                UserUtil.getUser().getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        MvcResult result = mockMvc.perform(get("/orders")
                        .param("page", "0")
                        .param("size", "1")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<OrderResponseDto> actual = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                        new TypeReference<>() {
                        });

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).status()).isEqualTo("PENDING");

    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get order items for valid order")
    void getOrderItems_ValidOrder_ShouldReturnOrderItems() throws Exception {
        Long orderId = 1L;

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                UserUtil.getUser(), null, UserUtil.getUser().getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        MvcResult result = mockMvc.perform(get("/orders/{orderId}/items", orderId)
                        .with(authentication(auth))
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<OrderItemResponseDto> actual = objectMapper.readValue(
                root.toString(), new TypeReference<>() {}
        );

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).quantity()).isGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get order items for non-existing order")
    void getOrderItems_NonExistingOrder_ShouldReturnNotFound() throws Exception {
        Long orderId = 999L;
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(UserUtil.getUser(), null,
                        UserUtil.getUser().getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/orders/{orderId}/items", orderId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update order status")
    void updateOrderStatus_ValidData_ShouldReturnUpdatedOrderStatus() throws Exception {
        Long orderId = 1L;
        UpdateOrderStatusRequestDto requestDto = new UpdateOrderStatusRequestDto(Status.COMPLETED);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(patch("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andReturn();

        UpdateOrderStatusRequestDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UpdateOrderStatusRequestDto.class
        );

        assertThat(actual.status()).isEqualTo(Status.COMPLETED);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update order status for non-existing order")
    void updateOrderStatus_NonExistingOrder_ShouldReturnNotFound() throws Exception {
        Long orderId = 999L;
        UpdateOrderStatusRequestDto requestDto = new UpdateOrderStatusRequestDto(Status.COMPLETED);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(patch("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Update order status when user is not admin")
    void updateOrderStatus_NoAdminRole_ShouldReturnForbidden() throws Exception {
        Long orderId = 1L;
        UpdateOrderStatusRequestDto requestDto = new UpdateOrderStatusRequestDto(Status.COMPLETED);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(patch("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Check data is cleaned up after tests")
    void checkDataCleanUp_ShouldReturnEmptyOrders() throws Exception {
        teardown(dataSource);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        UserUtil.getUser(), null, UserUtil.getUser().getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(auth);

        MvcResult result = mockMvc.perform(get("/orders")
                        .with(authentication(auth))
                        .param("page", "0")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<OrderResponseDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertThat(actual).isEmpty();
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(DELETE_ALL_DATA_SQL));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }
}
