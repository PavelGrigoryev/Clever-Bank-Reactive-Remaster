package com.grigoryev.cleverbankreactiveremaster.integration.controller;

import com.grigoryev.cleverbankreactiveremaster.dto.DeleteResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.user.UserRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.user.UserResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.handler.ExceptionResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.handler.ValidationErrorResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.handler.Violation;
import com.grigoryev.cleverbankreactiveremaster.integration.BaseIntegrationTest;
import com.grigoryev.cleverbankreactiveremaster.util.impl.UserRequestTestBuilder;
import com.grigoryev.cleverbankreactiveremaster.util.impl.UserResponseTestBuilder;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@AutoConfigureWebTestClient
class UserControllerTest extends BaseIntegrationTest {

    private final WebTestClient webTestClient;
    private static final String USERS = "/users";

    @Nested
    class FindByIdGetEndpointTest {

        @Test
        @DisplayName("test should return expected json and status 200")
        void testShouldReturnExpectedJsonAndStatus200() {
            UserResponse response = UserResponseTestBuilder.aUserResponse().build();

            webTestClient.get()
                    .uri(USERS + "/" + response.id())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(UserResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404")
        void testShouldReturnExpectedJsonAndStatus404() {
            long id = 567L;
            ExceptionResponse response = new ExceptionResponse("User with ID " + id + " is not found!");

            webTestClient.get()
                    .uri(USERS + "/" + id)
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
                    .uri(USERS + "/" + wrongId)
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
                    .uri(uriBuilder -> uriBuilder.path(USERS)
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
            UserResponse response = UserResponseTestBuilder.aUserResponse().build();

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(USERS)
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(UserResponse.class)
                    .contains(response);
        }

        @Test
        @DisplayName("test should return expected size and status 200")
        void testShouldReturnExpectedSizeAndStatus200() {
            int offset = 2;
            int limit = 5;
            int expectedSize = 5;

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(USERS)
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(UserResponse.class)
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
                    .uri(uriBuilder -> uriBuilder.path(USERS)
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
                    .uri(uriBuilder -> uriBuilder.path(USERS)
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
                    .uri(uriBuilder -> uriBuilder.path(USERS)
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
                    .uri(uriBuilder -> uriBuilder.path(USERS)
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
                    .uri(uriBuilder -> uriBuilder.path(USERS)
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
        @MethodSource("com.grigoryev.cleverbankreactiveremaster.integration.controller.UserControllerTest#getArgumentsForPostTest")
        void testShouldReturnExpectedJsonAndStatus201(Long id, UserRequest request) {
            UserResponse response = UserResponseTestBuilder.aUserResponse()
                    .withId(id)
                    .withLastname(request.lastname())
                    .withFirstname(request.firstname())
                    .withSurname(request.surname())
                    .withMobileNumber(request.mobileNumber())
                    .withRegisterDate(LocalDate.now())
                    .build();

            webTestClient.post()
                    .uri(USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(UserResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 400 if mobile number is not unique")
        void testShouldReturnExpectedJsonAndStatus400IfMobileNumberIsNotUnique() {
            UserRequest request = UserRequestTestBuilder.aUserRequest()
                    .withMobileNumber("+7 (900) 123-45-67")
                    .build();
            ExceptionResponse response = new ExceptionResponse("User with phone number "
                                                               + request.mobileNumber() + " is already exist");

            webTestClient.post()
                    .uri(USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if lastname is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfLastnameIsOutOfPattern() {
            UserRequest request = UserRequestTestBuilder.aUserRequest()
                    .withLastname("Мамкин666")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("lastname",
                            "This field must contain only letters of the Russian and English alphabets in any case")));

            webTestClient.post()
                    .uri(USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if firstname is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfFirstnameIsOutOfPattern() {
            UserRequest request = UserRequestTestBuilder.aUserRequest()
                    .withFirstname("Семён Ф")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("firstname",
                            "This field must contain only letters of the Russian and English alphabets in any case")));

            webTestClient.post()
                    .uri(USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if surname is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfSurnameIsOutOfPattern() {
            UserRequest request = UserRequestTestBuilder.aUserRequest()
                    .withSurname("Fedora:)")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("surname",
                            "This field must contain only letters of the Russian and English alphabets in any case")));

            webTestClient.post()
                    .uri(USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if mobile number is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfMobileNumberIsOutOfPattern() {
            UserRequest request = UserRequestTestBuilder.aUserRequest()
                    .withMobileNumber("5175")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("mobileNumber",
                            "Please enter a valid mobile number in the format +X (XXX) XXX-XX-XX or +XXX (XX) XXX-XX-XX")));

            webTestClient.post()
                    .uri(USERS)
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

        @ParameterizedTest(name = "{arguments} test")
        @DisplayName("test should return expected json and status 201")
        @MethodSource("com.grigoryev.cleverbankreactiveremaster.integration.controller.UserControllerTest#getArgumentsForPutTest")
        void testShouldReturnExpectedJsonAndStatus201(Long id, LocalDate registerDate, UserRequest request) {
            UserResponse response = UserResponseTestBuilder.aUserResponse()
                    .withId(id)
                    .withLastname(request.lastname())
                    .withFirstname(request.firstname())
                    .withSurname(request.surname())
                    .withMobileNumber(request.mobileNumber())
                    .withRegisterDate(registerDate)
                    .build();

            webTestClient.put()
                    .uri(USERS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(UserResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404")
        void testShouldReturnExpectedJsonAndStatus404() {
            long id = 567L;
            UserRequest request = UserRequestTestBuilder.aUserRequest().build();
            ExceptionResponse response = new ExceptionResponse("User with ID " + id + " is not found!");

            webTestClient.put()
                    .uri(USERS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 400 if mobile number is not unique")
        void testShouldReturnExpectedJsonAndStatus400IfMobileNumberIsNotUnique() {
            long id = 11L;
            UserRequest request = UserRequestTestBuilder.aUserRequest()
                    .withMobileNumber("+7 (900) 123-45-67")
                    .build();
            ExceptionResponse response = new ExceptionResponse("User with phone number "
                                                               + request.mobileNumber() + " is already exist");

            webTestClient.put()
                    .uri(USERS + "/" + id)
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
            UserRequest request = UserRequestTestBuilder.aUserRequest().build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("update.id", "must be greater than 0")));

            webTestClient.put()
                    .uri(USERS + "/" + wrongId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if lastname is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfLastnameIsOutOfPattern() {
            long id = 11L;
            UserRequest request = UserRequestTestBuilder.aUserRequest()
                    .withLastname("Мамкин666")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("lastname",
                            "This field must contain only letters of the Russian and English alphabets in any case")));

            webTestClient.put()
                    .uri(USERS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if firstname is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfFirstnameIsOutOfPattern() {
            long id = 11L;
            UserRequest request = UserRequestTestBuilder.aUserRequest()
                    .withFirstname("Семён Ф")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("firstname",
                            "This field must contain only letters of the Russian and English alphabets in any case")));

            webTestClient.put()
                    .uri(USERS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if surname is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfSurnameIsOutOfPattern() {
            long id = 11L;
            UserRequest request = UserRequestTestBuilder.aUserRequest()
                    .withSurname("Fedora:)")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("surname",
                            "This field must contain only letters of the Russian and English alphabets in any case")));

            webTestClient.put()
                    .uri(USERS + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if mobile number is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfMobileNumberIsOutOfPattern() {
            long id = 11L;
            UserRequest request = UserRequestTestBuilder.aUserRequest()
                    .withMobileNumber("5175")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("mobileNumber",
                            "Please enter a valid mobile number in the format +X (XXX) XXX-XX-XX or +XXX (XX) XXX-XX-XX")));

            webTestClient.put()
                    .uri(USERS + "/" + id)
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

        @ParameterizedTest(name = "{arguments} test")
        @DisplayName("test should return expected json and status 200")
        @MethodSource("com.grigoryev.cleverbankreactiveremaster.integration.controller.UserControllerTest#getArgumentsForDeleteTest")
        void testShouldReturnExpectedJsonAndStatus200(Long id) {
            DeleteResponse response = new DeleteResponse("User with ID " + id + " was successfully deleted");

            webTestClient.delete()
                    .uri(USERS + "/" + id)
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
            ExceptionResponse response = new ExceptionResponse("No User with ID " + id + " to delete");

            webTestClient.delete()
                    .uri(USERS + "/" + id)
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
                    .uri(USERS + "/" + wrongId)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

    }

    private static Stream<Arguments> getArgumentsForPostTest() {
        return Stream.of(
                Arguments.of(25L, UserRequestTestBuilder.aUserRequest().build()),
                Arguments.of(26L, UserRequestTestBuilder.aUserRequest()
                        .withLastname("Сильнейший")
                        .withFirstname("Александр")
                        .withSurname("Сергеевич")
                        .withMobileNumber("+7 (900) 333-11-75")
                        .build()));
    }

    private static Stream<Arguments> getArgumentsForPutTest() {
        return Stream.of(
                Arguments.of(5L, LocalDate.of(1994, 5, 5),
                        UserRequestTestBuilder.aUserRequest()
                                .withLastname("Сливовый")
                                .withFirstname("Алексей")
                                .withSurname("Васильевич")
                                .withMobileNumber("+7 (900) 666-58-96")
                                .build()),
                Arguments.of(6L, LocalDate.of(1995, 6, 6),
                        UserRequestTestBuilder.aUserRequest()
                                .withLastname("Железный")
                                .withFirstname("Василий")
                                .withSurname("Иванович")
                                .withMobileNumber("+7 (900) 256-17-56")
                                .build()));
    }

    private static Stream<Arguments> getArgumentsForDeleteTest() {
        return Stream.of(
                Arguments.of(15L),
                Arguments.of(16L),
                Arguments.of(13L)
        );
    }

}
