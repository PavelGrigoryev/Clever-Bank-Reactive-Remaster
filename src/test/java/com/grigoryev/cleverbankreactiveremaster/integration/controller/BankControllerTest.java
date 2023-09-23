package com.grigoryev.cleverbankreactiveremaster.integration.controller;

import com.grigoryev.cleverbankreactiveremaster.dto.DeleteResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.handler.ExceptionResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.handler.ValidationErrorResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.handler.Violation;
import com.grigoryev.cleverbankreactiveremaster.integration.BaseIntegrationTest;
import com.grigoryev.cleverbankreactiveremaster.util.impl.BankRequestTestBuilder;
import com.grigoryev.cleverbankreactiveremaster.util.impl.BankResponseTestBuilder;
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

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@AutoConfigureWebTestClient
public class BankControllerTest extends BaseIntegrationTest {

    private final WebTestClient webTestClient;
    private static final String BANKS = "/banks";

    @Nested
    class FindByIdGetEndpointTest {

        @Test
        @DisplayName("test should return expected json and status 200")
        void testShouldReturnExpectedJsonAndStatus200() {
            BankResponse response = BankResponseTestBuilder.aBankResponse().build();

            webTestClient.get()
                    .uri(BANKS + "/" + response.id())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(BankResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404")
        void testShouldReturnExpectedJsonAndStatus404() {
            long id = 567L;
            ExceptionResponse response = new ExceptionResponse("Bank with ID " + id + " is not found!");

            webTestClient.get()
                    .uri(BANKS + "/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if id is not positive")
        void testShouldReturnExpectedJsonAndStatus409IfIdIsNotPositive() {
            long wrongId = -1L;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("findByIdResponse.id", "must be greater than 0")));

            webTestClient.get()
                    .uri(BANKS + "/" + wrongId)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

    }

    @Nested
    class FindAllGetEndpointTest {

        @Test
        @DisplayName("test should return empty json and status 200")
        void testShouldReturnEmptyJsonAndStatus200() {
            int offset = 40;
            int limit = 3;
            String json = "[]";

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(BANKS)
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
            BankResponse response = BankResponseTestBuilder.aBankResponse().build();

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(BANKS)
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(BankResponse.class)
                    .contains(response);
        }

        @Test
        @DisplayName("test should return expected size and status 200")
        void testShouldReturnExpectedSizeAndStatus200() {
            int offset = 0;
            int limit = 5;
            int expectedSize = 5;

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(BANKS)
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(BankResponse.class)
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
                    .uri(uriBuilder -> uriBuilder.path(BANKS)
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
                    .uri(uriBuilder -> uriBuilder.path(BANKS)
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
                    .uri(uriBuilder -> uriBuilder.path(BANKS)
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
                    .uri(uriBuilder -> uriBuilder.path(BANKS)
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
                    .uri(uriBuilder -> uriBuilder.path(BANKS)
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
        @MethodSource("com.grigoryev.cleverbankreactiveremaster.integration.controller.BankControllerTest#getArgumentsForPostTest")
        void testShouldReturnExpectedJsonAndStatus201(Long id, BankRequest request) {
            BankResponse response = BankResponseTestBuilder.aBankResponse()
                    .withId(id)
                    .withName(request.name())
                    .withAddress(request.address())
                    .withPhoneNumber(request.phoneNumber())
                    .build();

            webTestClient.post()
                    .uri(BANKS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(BankResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 400 if phone number is not unique")
        void testShouldReturnExpectedJsonAndStatus400IfPhoneNumberIsNotUnique() {
            BankRequest request = BankRequestTestBuilder.aBankRequest()
                    .withPhoneNumber("+7 (495) 222-22-22")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Bank with phone number "
                                                               + request.phoneNumber() + " is already exist");

            webTestClient.post()
                    .uri(BANKS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if name is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfNameIsOutOfPattern() {
            BankRequest request = BankRequestTestBuilder.aBankRequest()
                    .withName("The Best Bank !")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("name",
                            "This field must contain letters of the Russian and English alphabets in any case and @_- with space symbols")));

            webTestClient.post()
                    .uri(BANKS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if address is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfAddressIsOutOfPattern() {
            BankRequest request = BankRequestTestBuilder.aBankRequest()
                    .withAddress("!_!")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("address",
                            "This field must contain letters of the Russian and English alphabets in any case and .,- with space symbols")));

            webTestClient.post()
                    .uri(BANKS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if phone number is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfPhoneNumberIsOutOfPattern() {
            BankRequest request = BankRequestTestBuilder.aBankRequest()
                    .withPhoneNumber("1111 - 25")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("phoneNumber",
                            "Please enter a valid mobile number in the format +X (XXX) XXX-XX-XX or +XXX (XX) XXX-XX-XX")));

            webTestClient.post()
                    .uri(BANKS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

    }

    @Nested
    class UpdatePutEndpointTest {

        @Test
        @DisplayName("test should return expected json and status 201")
        void testShouldReturnExpectedJsonAndStatus201() {
            long id = 7L;
            BankRequest request = BankRequestTestBuilder.aBankRequest()
                    .withName("Странный Банк")
                    .withAddress("ул. Афанасьева, 11")
                    .withPhoneNumber("+7 (495) 777-55-11")
                    .build();
            BankResponse response = BankResponseTestBuilder.aBankResponse()
                    .withId(id)
                    .withName(request.name())
                    .withAddress(request.address())
                    .withPhoneNumber(request.phoneNumber())
                    .build();

            webTestClient.put()
                    .uri(BANKS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(BankResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404")
        void testShouldReturnExpectedJsonAndStatus404() {
            long id = 567L;
            BankRequest request = BankRequestTestBuilder.aBankRequest().build();
            ExceptionResponse response = new ExceptionResponse("Bank with ID " + id + " is not found!");

            webTestClient.put()
                    .uri(BANKS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 400 if phone number is not unique")
        void testShouldReturnExpectedJsonAndStatus400IfPhoneNumberIsNotUnique() {
            long id = 4L;
            BankRequest request = BankRequestTestBuilder.aBankRequest()
                    .withPhoneNumber("+7 (495) 222-22-22")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Bank with phone number "
                                                               + request.phoneNumber() + " is already exist");

            webTestClient.put()
                    .uri(BANKS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if id is not positive")
        void testShouldReturnExpectedJsonAndStatus409IfIdIsNotPositive() {
            long wrongId = -1L;
            BankRequest request = BankRequestTestBuilder.aBankRequest().build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("update.id", "must be greater than 0")));

            webTestClient.put()
                    .uri(BANKS + "/" + wrongId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if name is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfNameIsOutOfPattern() {
            long id = 11L;
            BankRequest request = BankRequestTestBuilder.aBankRequest()
                    .withName("The Best Bank !")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("name",
                            "This field must contain letters of the Russian and English alphabets in any case and @_- with space symbols")));

            webTestClient.put()
                    .uri(BANKS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if address is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfAddressIsOutOfPattern() {
            long id = 11L;
            BankRequest request = BankRequestTestBuilder.aBankRequest()
                    .withAddress("!_!")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("address",
                            "This field must contain letters of the Russian and English alphabets in any case and .,- with space symbols")));

            webTestClient.put()
                    .uri(BANKS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if phone number is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfPhoneNumberIsOutOfPattern() {
            long id = 11L;
            BankRequest request = BankRequestTestBuilder.aBankRequest()
                    .withPhoneNumber("1111 - 25")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("phoneNumber",
                            "Please enter a valid mobile number in the format +X (XXX) XXX-XX-XX or +XXX (XX) XXX-XX-XX")));

            webTestClient.put()
                    .uri(BANKS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

    }

    @Nested
    class DeleteByIdEndpointTest {

        @Test
        @DisplayName("test should return expected json and status 200")
        void testShouldReturnExpectedJsonAndStatus200() {
            long id = 8L;
            DeleteResponse response = new DeleteResponse("Bank with ID " + id + " was successfully deleted");

            webTestClient.delete()
                    .uri(BANKS + "/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(DeleteResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404")
        void testShouldReturnExpectedJsonAndStatus404() {
            long id = 567L;
            ExceptionResponse response = new ExceptionResponse("No Bank with ID " + id + " to delete");

            webTestClient.delete()
                    .uri(BANKS + "/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if id is not positive")
        void testShouldReturnExpectedJsonAndStatus409IfIdIsNotPositive() {
            long wrongId = -1L;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("delete.id", "must be greater than 0")));

            webTestClient.delete()
                    .uri(BANKS + "/" + wrongId)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

    }

    private static Stream<Arguments> getArgumentsForPostTest() {
        return Stream.of(
                Arguments.of(9L, BankRequestTestBuilder.aBankRequest().build()),
                Arguments.of(10L, BankRequestTestBuilder.aBankRequest()
                        .withName("Новый Банк")
                        .withAddress("ул. Больших челнов, 25")
                        .withPhoneNumber("+7 (495) 777-13-11")
                        .build()));
    }

}
