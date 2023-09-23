package com.grigoryev.cleverbankreactiveremaster.integration.controller;

import com.grigoryev.cleverbankreactiveremaster.dto.DeleteResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.handler.ExceptionResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.handler.ValidationErrorResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.handler.Violation;
import com.grigoryev.cleverbankreactiveremaster.integration.BaseIntegrationTest;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.util.impl.AccountRequestTestBuilder;
import com.grigoryev.cleverbankreactiveremaster.util.impl.AccountResponseTestBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@AutoConfigureWebTestClient
public class AccountControllerTest extends BaseIntegrationTest {

    private final WebTestClient webTestClient;
    private static final String ACCOUNTS = "/accounts";

    @Nested
    class FindByIdGetEndpointTest {

        @Test
        @DisplayName("test should return expected json and status 200")
        void testShouldReturnExpectedJsonAndStatus200() {
            AccountResponse response = AccountResponseTestBuilder.aAccountResponse().build();

            webTestClient.get()
                    .uri(ACCOUNTS + "/" + response.id())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(AccountResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404")
        void testShouldReturnExpectedJsonAndStatus404() {
            long id = 567L;
            ExceptionResponse response = new ExceptionResponse("Account with ID " + id + " is not found!");

            webTestClient.get()
                    .uri(ACCOUNTS + "/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

    }

    @Nested
    class FindAllGetEndpointTest {

        @Test
        @DisplayName("test should return empty json and status 200")
        void testShouldReturnEmptyJsonAndStatus200() {
            int offset = 60;
            int limit = 3;
            String json = "[]";

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(ACCOUNTS)
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .json(json);
        }

        @Test
        @DisplayName("test should return expected json and status 200")
        void testShouldReturnExpectedJsonAndStatus200() {
            int offset = 0;
            int limit = 1;
            AccountResponse response = AccountResponseTestBuilder.aAccountResponse().build();

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(ACCOUNTS)
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(AccountResponse.class)
                    .contains(response);
        }

        @Test
        @DisplayName("test should return expected size and status 200")
        void testShouldReturnExpectedSizeAndStatus200() {
            int offset = 0;
            int limit = 5;
            int expectedSize = 5;

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(ACCOUNTS)
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(AccountResponse.class)
                    .hasSize(expectedSize);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if offset is not positive or zero")
        void testShouldReturnExpectedJsonAndStatus409IfOffsetIsNotPositiveOrZero() {
            int offset = -1;
            int limit = 5;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("offset", "must be greater than or equal to 0")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(ACCOUNTS)
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if offset is null")
        void testShouldReturnExpectedJsonAndStatus409IfOffsetIsNull() {
            int limit = 5;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("offset", "must not be null")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(ACCOUNTS)
                            .queryParam("limit", limit)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if limit is null")
        void testShouldReturnExpectedJsonAndStatus409IfLimitIsNull() {
            int offset = 5;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("limit", "must not be null")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(ACCOUNTS)
                            .queryParam("offset", offset)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if limit is less than one")
        void testShouldReturnExpectedJsonAndStatus409IfLimitIsLessThanOne() {
            int offset = 0;
            int limit = 0;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("limit", "must be greater than or equal to 1")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(ACCOUNTS)
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if limit is greater than twenty")
        void testShouldReturnExpectedJsonAndStatus409IfLimitIsGreaterThanTwenty() {
            int offset = 0;
            int limit = 21;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("limit", "must be less than or equal to 20")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(ACCOUNTS)
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

    }

    @Nested
    class SavePostEndpointTest {

        @ParameterizedTest(name = "{arguments} test")
        @DisplayName("test should return expected json and status 201")
        @MethodSource("com.grigoryev.cleverbankreactiveremaster.integration.controller.AccountControllerTest#getArgumentsForPostTest")
        void testShouldReturnExpectedJsonAndStatus201(AccountRequest request) {
            webTestClient.post()
                    .uri(ACCOUNTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.currency").isEqualTo(request.currency())
                    .jsonPath("$.balance").isEqualTo(request.balance())
                    .jsonPath("$.opening_date").isEqualTo(LocalDate.now().toString())
                    .jsonPath("$.closing_date").isEmpty()
                    .jsonPath("$.bank.id").isEqualTo(request.bankId())
                    .jsonPath("$.user.id").isEqualTo(request.userId());
        }

        @Test
        @DisplayName("test should return expected json and status 404 if user is not found by id")
        void testShouldReturnExpectedJsonAndStatus404IfUserIsNotFoundById() {
            AccountRequest request = AccountRequestTestBuilder.aAccountRequest()
                    .withUserId(567L)
                    .build();
            ExceptionResponse response = new ExceptionResponse("User with ID " + request.userId() + " is not found!");

            webTestClient.post()
                    .uri(ACCOUNTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404 if bank is not found by id")
        void testShouldReturnExpectedJsonAndStatus404IfBankIsNotFoundById() {
            AccountRequest request = AccountRequestTestBuilder.aAccountRequest()
                    .withBankId(167L)
                    .build();
            ExceptionResponse response = new ExceptionResponse("Bank with ID " + request.bankId() + " is not found!");

            webTestClient.post()
                    .uri(ACCOUNTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if currency is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfCurrencyIsOutOfPattern() {
            AccountRequest request = AccountRequestTestBuilder.aAccountRequest()
                    .withCurrency("ASD")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("currency",
                            "Available currencies are: BYN, RUB, USD or EUR")));

            webTestClient.post()
                    .uri(ACCOUNTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if balance is not positive")
        void testShouldReturnExpectedJsonAndStatus409IfBalanceIsNotPositive() {
            AccountRequest request = AccountRequestTestBuilder.aAccountRequest()
                    .withBalance(BigDecimal.valueOf(-25))
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("balance", "must be greater than 0")));

            webTestClient.post()
                    .uri(ACCOUNTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if balance digits fraction is more than two")
        void testShouldReturnExpectedJsonAndStatus409IfBalanceDigitsFractionIsMoreThanTwo() {
            AccountRequest request = AccountRequestTestBuilder.aAccountRequest()
                    .withBalance(BigDecimal.valueOf(565.235))
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("balance", "numeric value out of bounds (<10 digits>.<2 digits> expected)")));

            webTestClient.post()
                    .uri(ACCOUNTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if balance digits integer is more than ten")
        void testShouldReturnExpectedJsonAndStatus409IfBalanceDigitsIntegerIsMoreThanTen() {
            AccountRequest request = AccountRequestTestBuilder.aAccountRequest()
                    .withBalance(BigDecimal.valueOf(56525675271.2))
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("balance", "numeric value out of bounds (<10 digits>.<2 digits> expected)")));

            webTestClient.post()
                    .uri(ACCOUNTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if bankId is not positive")
        void testShouldReturnExpectedJsonAndStatus409IfBankIdIsNotPositive() {
            AccountRequest request = AccountRequestTestBuilder.aAccountRequest()
                    .withBankId(-3L)
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("bankId", "must be greater than 0")));

            webTestClient.post()
                    .uri(ACCOUNTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if userId is not positive")
        void testShouldReturnExpectedJsonAndStatus409IfUserIdIsNotPositive() {
            AccountRequest request = AccountRequestTestBuilder.aAccountRequest()
                    .withUserId(-3L)
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("userId", "must be greater than 0")));

            webTestClient.post()
                    .uri(ACCOUNTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

    }

    @Nested
    class CloseAccountPutEndpointTest {

        @Test
        @DisplayName("test should return expected json and status 201")
        void testShouldReturnExpectedJsonAndStatus201() {
            String id = "SW5C MJDI ZZN0 CTUW 5MEO 8DRA GKU2";

            webTestClient.put()
                    .uri(ACCOUNTS + "/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(id)
                    .jsonPath("$.balance").isEqualTo(BigDecimal.ZERO)
                    .jsonPath("$.closing_date").isEqualTo(LocalDate.now().toString());
        }

        @Test
        @DisplayName("test should return expected json and status 404")
        void testShouldReturnExpectedJsonAndStatus404() {
            long id = 567L;
            ExceptionResponse response = new ExceptionResponse("Account with ID " + id + " is not found!");

            webTestClient.put()
                    .uri(ACCOUNTS + "/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

    }

    @Nested
    class DeleteByIdEndpointTest {

        @Test
        @DisplayName("test should return expected json and status 200")
        void testShouldReturnExpectedJsonAndStatus200() {
            String id = "NFXS FJGQ 5FL6 XF1S D88V 9W1Q G19O";
            DeleteResponse response = new DeleteResponse("Account with ID " + id + " was successfully deleted");

            webTestClient.delete()
                    .uri(ACCOUNTS + "/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(DeleteResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404")
        void testShouldReturnExpectedJsonAndStatus404() {
            String id = "AHTU FJGQ 5FL6 XF1S D88V 9W1Q G133";
            ExceptionResponse response = new ExceptionResponse("No Account with ID " + id + " to delete");

            webTestClient.delete()
                    .uri(ACCOUNTS + "/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

    }

    private static Stream<Arguments> getArgumentsForPostTest() {
        return Stream.of(
                Arguments.of(AccountRequestTestBuilder.aAccountRequest().build()),
                Arguments.of(AccountRequestTestBuilder.aAccountRequest()
                        .withCurrency(Currency.BYN.toString())
                        .withBalance(BigDecimal.ONE)
                        .withBankId(4L)
                        .withUserId(10L)
                        .build()));
    }

}
