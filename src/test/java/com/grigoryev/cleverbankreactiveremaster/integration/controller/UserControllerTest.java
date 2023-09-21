package com.grigoryev.cleverbankreactiveremaster.integration.controller;

import com.grigoryev.cleverbankreactiveremaster.dto.user.UserResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.handler.ExceptionResponse;
import com.grigoryev.cleverbankreactiveremaster.integration.BaseIntegrationTest;
import com.grigoryev.cleverbankreactiveremaster.util.impl.UserResponseTestBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@RequiredArgsConstructor
@AutoConfigureWebTestClient
class UserControllerTest extends BaseIntegrationTest {

    private final WebTestClient webTestClient;

    @Nested
    class FindByIdGetEndpointTest {

        @Test
        @DisplayName("test should return expected json and status 200")
        void testShouldReturnExpectedJsonAndStatus200() {
            UserResponse response = UserResponseTestBuilder.aUserResponse().build();

            webTestClient.get()
                    .uri("/users/" + response.id())
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
                    .uri("/users/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

    }

}
