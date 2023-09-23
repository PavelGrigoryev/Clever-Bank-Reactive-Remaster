package com.grigoryev.cleverbankreactiveremaster.integration.controller;

import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.AmountStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ExchangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.handler.ExceptionResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.handler.ValidationErrorResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.handler.Violation;
import com.grigoryev.cleverbankreactiveremaster.integration.BaseIntegrationTest;
import com.grigoryev.cleverbankreactiveremaster.model.Type;
import com.grigoryev.cleverbankreactiveremaster.util.impl.AmountStatementResponseTestBuilder;
import com.grigoryev.cleverbankreactiveremaster.util.impl.ChangeBalanceRequestTestBuilder;
import com.grigoryev.cleverbankreactiveremaster.util.impl.ChangeBalanceResponseTestBuilder;
import com.grigoryev.cleverbankreactiveremaster.util.impl.ExchangeBalanceResponseTestBuilder;
import com.grigoryev.cleverbankreactiveremaster.util.impl.TransactionResponseTestBuilder;
import com.grigoryev.cleverbankreactiveremaster.util.impl.TransactionStatementRequestTestBuilder;
import com.grigoryev.cleverbankreactiveremaster.util.impl.TransactionStatementResponseTestBuilder;
import com.grigoryev.cleverbankreactiveremaster.util.impl.TransferBalanceRequestTestBuilder;
import com.grigoryev.cleverbankreactiveremaster.util.impl.TransferBalanceResponseTestBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@RequiredArgsConstructor
@AutoConfigureWebTestClient
class TransactionControllerTest extends BaseIntegrationTest {

    private final WebTestClient webTestClient;
    private static final String TRANSACTIONS = "/transactions";

    @Nested
    class ChangeBalancePostEndPointTest {

        @Test
        @DisplayName("test should replenish sum return expected json and status 201")
        void testShouldReplenishSumReturnExpectedJsonAndStatus201() {
            ChangeBalanceRequest request = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest().build();
            ChangeBalanceResponse response = ChangeBalanceResponseTestBuilder.aChangeBalanceResponse().build();

            webTestClient.post()
                    .uri(TRANSACTIONS + "/change")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.date").isEqualTo(response.date().toString())
                    .jsonPath("$.time").isNotEmpty()
                    .jsonPath("$.currency").isEqualTo(response.currency().toString())
                    .jsonPath("$.type").isEqualTo(response.type().toString())
                    .jsonPath("$.bank_sender_name").isEqualTo(response.bankSenderName())
                    .jsonPath("$.bank_recipient_name").isEqualTo(response.bankRecipientName())
                    .jsonPath("$.account_recipient_id").isEqualTo(response.accountRecipientId())
                    .jsonPath("$.sum").isEqualTo(response.sum())
                    .jsonPath("$.old_balance").isEqualTo(response.oldBalance())
                    .jsonPath("$.new_balance").isEqualTo(response.newBalance());
        }

        @Test
        @DisplayName("test should withdrawal sum return expected json and status 201")
        void testShouldWithdrawalSumReturnExpectedJsonAndStatus201() {
            ChangeBalanceRequest request = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest()
                    .withAccountRecipientId("ZMEJ L8W1 YNCU JRK6 XOYG Z4R1 IDIJ")
                    .withType(Type.WITHDRAWAL.toString())
                    .build();
            ChangeBalanceResponse response = ChangeBalanceResponseTestBuilder.aChangeBalanceResponse()
                    .withAccountRecipientId(request.accountRecipientId())
                    .withType(Type.WITHDRAWAL)
                    .withOldBalance(new BigDecimal("1000.0"))
                    .build();

            webTestClient.post()
                    .uri(TRANSACTIONS + "/change")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.date").isEqualTo(response.date().toString())
                    .jsonPath("$.time").isNotEmpty()
                    .jsonPath("$.currency").isEqualTo(response.currency().toString())
                    .jsonPath("$.type").isEqualTo(response.type().toString())
                    .jsonPath("$.bank_sender_name").isEqualTo(response.bankSenderName())
                    .jsonPath("$.bank_recipient_name").isEqualTo(response.bankRecipientName())
                    .jsonPath("$.account_recipient_id").isEqualTo(response.accountRecipientId())
                    .jsonPath("$.sum").isEqualTo(response.sum())
                    .jsonPath("$.old_balance").isEqualTo(response.oldBalance())
                    .jsonPath("$.new_balance").isEqualTo(response.oldBalance().subtract(response.sum()));
        }

        @Test
        @DisplayName("test should return expected json and status 400 if account is closed")
        void testShouldReturnExpectedJsonAndStatus404IfAccountIsClosed() {
            ChangeBalanceRequest request = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest()
                    .withAccountRecipientId("KLX4 E9NX 5MAC 06XB EAD7 4BLM SD1V")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Account with ID " + request.accountRecipientId()
                                                               + " is closed since 2020-12-31");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/change")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 400 if account has sufficient balance")
        void testShouldReturnExpectedJsonAndStatus404IfAccountHasSufficientBalance() {
            ChangeBalanceRequest request = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest()
                    .withAccountRecipientId("5X92 ISKH ZUAT 2YF5 D0A9 C2Z4 7UIZ")
                    .withSum(new BigDecimal("1500.01"))
                    .withType(Type.WITHDRAWAL.toString())
                    .build();
            ExceptionResponse response = new ExceptionResponse("Insufficient funds in the account! You want to change "
                                                               + request.sum() + ", but you have only 1500.00");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/change")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404")
        void testShouldReturnExpectedJsonAndStatus404() {
            ChangeBalanceRequest request = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest()
                    .withAccountRecipientId("AGA")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Account with ID " + request.accountRecipientId() + " is not found!");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/change")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if type is out of pattern")
        void testShouldReturnExpectedJsonAndStatus409IfTypeIsOutOfPattern() {
            ChangeBalanceRequest request = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest()
                    .withType("NEED_GOLD")
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("type",
                            "Available types are: REPLENISHMENT or WITHDRAWAL")));

            webTestClient.post()
                    .uri(TRANSACTIONS + "/change")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if sum is not positive")
        void testShouldReturnExpectedJsonAndStatus409IfSumIsNotPositive() {
            ChangeBalanceRequest request = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest()
                    .withSum(BigDecimal.valueOf(-45))
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("sum", "must be greater than 0")));

            webTestClient.post()
                    .uri(TRANSACTIONS + "/change")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if sum digits fraction is more than two")
        void testShouldReturnExpectedJsonAndStatus409IfSumDigitsFractionIsMoreThanTwo() {
            ChangeBalanceRequest request = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest()
                    .withSum(BigDecimal.valueOf(256.325))
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("sum", "numeric value out of bounds (<10 digits>.<2 digits> expected)")));

            webTestClient.post()
                    .uri(TRANSACTIONS + "/change")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if sum digits integer is more than ten")
        void testShouldReturnExpectedJsonAndStatus409IfSumDigitsIntegerIsMoreThanTen() {
            ChangeBalanceRequest request = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest()
                    .withSum(BigDecimal.valueOf(32654985265.23))
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("sum", "numeric value out of bounds (<10 digits>.<2 digits> expected)")));

            webTestClient.post()
                    .uri(TRANSACTIONS + "/change")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

    }

    @Nested
    class TransferBalancePostEndPointTest {

        @Test
        @DisplayName("test should return expected json and status 201")
        void testShouldReturnExpectedJsonAndStatus201() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest().build();
            TransferBalanceResponse response = TransferBalanceResponseTestBuilder.aTransferBalanceResponse().build();

            webTestClient.post()
                    .uri(TRANSACTIONS + "/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.date").isEqualTo(response.date().toString())
                    .jsonPath("$.time").isNotEmpty()
                    .jsonPath("$.currency").isEqualTo(response.currency().toString())
                    .jsonPath("$.type").isEqualTo(response.type().toString())
                    .jsonPath("$.bank_sender_name").isEqualTo(response.bankSenderName())
                    .jsonPath("$.bank_recipient_name").isEqualTo(response.bankRecipientName())
                    .jsonPath("$.account_sender_id").isEqualTo(response.accountSenderId())
                    .jsonPath("$.account_recipient_id").isEqualTo(response.accountRecipientId())
                    .jsonPath("$.sum").isEqualTo(response.sum())
                    .jsonPath("$.sender_old_balance").isEqualTo(response.senderOldBalance())
                    .jsonPath("$.sender_new_balance").isEqualTo(response.senderNewBalance())
                    .jsonPath("$.recipient_old_balance").isEqualTo(response.recipientOldBalance())
                    .jsonPath("$.recipient_new_balance").isEqualTo(response.recipientNewBalance());
        }

        @Test
        @DisplayName("test should return expected json and status 400 if account sender is closed")
        void testShouldReturnExpectedJsonAndStatus404IfAccountSenderIsClosed() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withAccountSenderId("KLX4 E9NX 5MAC 06XB EAD7 4BLM SD1V")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Account with ID " + request.accountSenderId()
                                                               + " is closed since 2020-12-31");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/transfer")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 400 if account recipient is closed")
        void testShouldReturnExpectedJsonAndStatus404IfAccountRecipientIsClosed() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withAccountSenderId("FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62")
                    .withAccountRecipientId("KLX4 E9NX 5MAC 06XB EAD7 4BLM SD1V")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Account with ID " + request.accountRecipientId()
                                                               + " is closed since 2020-12-31");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/transfer")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 400 if account has sufficient balance")
        void testShouldReturnExpectedJsonAndStatus404IfAccountHasSufficientBalance() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withAccountSenderId("MU1Y 7LTU 7QLR 14XD 2789 T5MM XRXU")
                    .withAccountRecipientId("2Z5V 3CHL M1Q9 6SCS MJDD DOEQ OL4Y")
                    .withSum(new BigDecimal("2000.01"))
                    .build();
            ExceptionResponse response = new ExceptionResponse("Insufficient funds in the account! You want to change "
                                                               + request.sum() + ", but you have only 2000.00");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/transfer")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 400 if accounts is not equal by currency")
        void testShouldReturnExpectedJsonAndStatus404IfAccountsIsNotEqualByCurrency() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withAccountSenderId("MU1Y 7LTU 7QLR 14XD 2789 T5MM XRXU")
                    .withAccountRecipientId("FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Your currency is USD, but account currency is EUR");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/transfer")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404 if account sender is not found")
        void testShouldReturnExpectedJsonAndStatus404IfAccountSenderIsNotFound() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withAccountSenderId("MU1Y 7LTU 7QLR 14XD 2789 T5MM XR")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Account with ID " + request.accountSenderId() + " is not found!");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404 if account recipient is not found")
        void testShouldReturnExpectedJsonAndStatus404IfAccountRecipientIsNotFound() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withAccountSenderId("MU1Y 7LTU 7QLR 14XD 2789 T5MM XRXU")
                    .withAccountRecipientId("FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Account with ID " + request.accountRecipientId() + " is not found!");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if sum is not positive")
        void testShouldReturnExpectedJsonAndStatus409IfSumIsNotPositive() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withSum(BigDecimal.valueOf(-45))
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("sum", "must be greater than 0")));

            webTestClient.post()
                    .uri(TRANSACTIONS + "/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if sum digits fraction is more than two")
        void testShouldReturnExpectedJsonAndStatus409IfSumDigitsFractionIsMoreThanTwo() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withSum(BigDecimal.valueOf(256.325))
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("sum", "numeric value out of bounds (<10 digits>.<2 digits> expected)")));

            webTestClient.post()
                    .uri(TRANSACTIONS + "/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if sum digits integer is more than ten")
        void testShouldReturnExpectedJsonAndStatus409IfSumDigitsIntegerIsMoreThanTen() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withSum(BigDecimal.valueOf(32654985265.23))
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("sum", "numeric value out of bounds (<10 digits>.<2 digits> expected)")));

            webTestClient.post()
                    .uri(TRANSACTIONS + "/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

    }

    @Nested
    class ExchangeBalancePostEndPointTest {

        @Test
        @DisplayName("test should return expected json and status 201")
        void testShouldReturnExpectedJsonAndStatus201() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withAccountSenderId("QR2Q PA57 LB3E LHT3 HCZ2 V4MV XL6M")
                    .withAccountRecipientId("19CM 9B6S FFF7 0N1Y M8UY AXCE RMJV")
                    .withSum(new BigDecimal("3000"))
                    .build();
            ExchangeBalanceResponse response = ExchangeBalanceResponseTestBuilder.aExchangeBalanceResponse().build();

            webTestClient.post()
                    .uri(TRANSACTIONS + "/exchange")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.date").isEqualTo(response.date().toString())
                    .jsonPath("$.time").isNotEmpty()
                    .jsonPath("$.currency_sender").isEqualTo(response.currencySender().toString())
                    .jsonPath("$.currency_recipient").isEqualTo(response.currencyRecipient().toString())
                    .jsonPath("$.type").isEqualTo(response.type().toString())
                    .jsonPath("$.bank_sender_name").isEqualTo(response.bankSenderName())
                    .jsonPath("$.bank_recipient_name").isEqualTo(response.bankRecipientName())
                    .jsonPath("$.account_sender_id").isEqualTo(response.accountSenderId())
                    .jsonPath("$.account_recipient_id").isEqualTo(response.accountRecipientId())
                    .jsonPath("$.sum_sender").isEqualTo(response.sumSender())
                    .jsonPath("$.sum_recipient").isEqualTo(response.sumRecipient())
                    .jsonPath("$.sender_old_balance").isEqualTo(response.senderOldBalance())
                    .jsonPath("$.sender_new_balance").isEqualTo(response.senderNewBalance())
                    .jsonPath("$.recipient_old_balance").isEqualTo(response.recipientOldBalance())
                    .jsonPath("$.recipient_new_balance").isEqualTo(response.recipientNewBalance());
        }

        @Test
        @DisplayName("test should return expected json and status 400 if account sender is closed")
        void testShouldReturnExpectedJsonAndStatus404IfAccountSenderIsClosed() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withAccountSenderId("KLX4 E9NX 5MAC 06XB EAD7 4BLM SD1V")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Account with ID " + request.accountSenderId()
                                                               + " is closed since 2020-12-31");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/exchange")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 400 if account recipient is closed")
        void testShouldReturnExpectedJsonAndStatus404IfAccountRecipientIsClosed() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withAccountSenderId("FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62")
                    .withAccountRecipientId("KLX4 E9NX 5MAC 06XB EAD7 4BLM SD1V")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Account with ID " + request.accountRecipientId()
                                                               + " is closed since 2020-12-31");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/exchange")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 400 if account has sufficient balance")
        void testShouldReturnExpectedJsonAndStatus404IfAccountHasSufficientBalance() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withAccountSenderId("MU1Y 7LTU 7QLR 14XD 2789 T5MM XRXU")
                    .withAccountRecipientId("2Z5V 3CHL M1Q9 6SCS MJDD DOEQ OL4Y")
                    .withSum(new BigDecimal("2000.01"))
                    .build();
            ExceptionResponse response = new ExceptionResponse("Insufficient funds in the account! You want to change "
                                                               + request.sum() + ", but you have only 2000.00");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/exchange")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404 if account sender is not found")
        void testShouldReturnExpectedJsonAndStatus404IfAccountSenderIsNotFound() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withAccountSenderId("MU1Y 7LTU 7QLR 14XD 2789 T5MM XR")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Account with ID " + request.accountSenderId() + " is not found!");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/exchange")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404 if account recipient is not found")
        void testShouldReturnExpectedJsonAndStatus404IfAccountRecipientIsNotFound() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withAccountSenderId("MU1Y 7LTU 7QLR 14XD 2789 T5MM XRXU")
                    .withAccountRecipientId("FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Account with ID " + request.accountRecipientId() + " is not found!");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/exchange")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if sum is not positive")
        void testShouldReturnExpectedJsonAndStatus409IfSumIsNotPositive() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withSum(BigDecimal.valueOf(-45))
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("sum", "must be greater than 0")));

            webTestClient.post()
                    .uri(TRANSACTIONS + "/exchange")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if sum digits fraction is more than two")
        void testShouldReturnExpectedJsonAndStatus409IfSumDigitsFractionIsMoreThanTwo() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withSum(BigDecimal.valueOf(256.325))
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("sum", "numeric value out of bounds (<10 digits>.<2 digits> expected)")));

            webTestClient.post()
                    .uri(TRANSACTIONS + "/exchange")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 409 if sum digits integer is more than ten")
        void testShouldReturnExpectedJsonAndStatus409IfSumDigitsIntegerIsMoreThanTen() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withSum(BigDecimal.valueOf(32654985265.23))
                    .build();
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("sum", "numeric value out of bounds (<10 digits>.<2 digits> expected)")));

            webTestClient.post()
                    .uri(TRANSACTIONS + "/exchange")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

    }

    @Nested
    class FindAllByPeriodOfDateAndAccountIdPostEndpointTest {

        @Test
        @DisplayName("test should return expected json and status 201")
        void testShouldReturnExpectedJsonAndStatus201() {
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest().build();
            TransactionStatementResponse response = TransactionStatementResponseTestBuilder.aTransactionStatementResponse().build();

            webTestClient.post()
                    .uri(TRANSACTIONS + "/statement")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.bank_name").isEqualTo(response.bankName())
                    .jsonPath("$.lastname").isEqualTo(response.lastname())
                    .jsonPath("$.firstname").isEqualTo(response.firstname())
                    .jsonPath("$.surname").isEqualTo(response.surname())
                    .jsonPath("$.account_id").isEqualTo(response.accountId())
                    .jsonPath("$.currency").isEqualTo(response.currency().toString())
                    .jsonPath("$.opening_date").isEqualTo(response.openingDate().toString())
                    .jsonPath("$.from").isEqualTo(response.from().toString())
                    .jsonPath("$.to").isEqualTo(response.to().toString())
                    .jsonPath("$.formation_date").isEqualTo(response.formationDate().toString())
                    .jsonPath("$.formation_time").isNotEmpty()
                    .jsonPath("$.balance").isEqualTo(response.balance())
                    .jsonPath("$.transactions.size()").isEqualTo(3);
        }

        @Test
        @DisplayName("test should return expected size and status 404 if no transactions found")
        void testShouldReturnExpectedSizeAndStatus404IfNoTransactionsFound() {
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest()
                    .withFrom(LocalDate.of(2023, Month.MARCH, 15))
                    .withTo(LocalDate.of(2023, Month.MARCH, 18))
                    .build();
            ExceptionResponse response = new ExceptionResponse("It is not possible to create a transaction statement because" +
                                                               " you do not have any transactions for this period of time : from "
                                                               + request.from() + " to " + request.to());

            webTestClient.post()
                    .uri(TRANSACTIONS + "/statement")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected size and status 404 if no account found")
        void testShouldReturnExpectedSizeAndStatus404IfNoAccountFound() {
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest()
                    .withAccountId("ASSA")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Account with ID " + request.accountId() + " is not found!");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/statement")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

    }

    @Nested
    class FindSumOfFundsByPeriodOfDateAndAccountIdPostEndpointTest {

        @Test
        @DisplayName("test should return expected json and status 201")
        void testShouldReturnExpectedJsonAndStatus201() {
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest().build();
            AmountStatementResponse response = AmountStatementResponseTestBuilder.aAmountStatementResponse().build();

            webTestClient.post()
                    .uri(TRANSACTIONS + "/amount")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.bank_name").isEqualTo(response.bankName())
                    .jsonPath("$.lastname").isEqualTo(response.lastname())
                    .jsonPath("$.firstname").isEqualTo(response.firstname())
                    .jsonPath("$.surname").isEqualTo(response.surname())
                    .jsonPath("$.account_id").isEqualTo(response.accountId())
                    .jsonPath("$.currency").isEqualTo(response.currency().toString())
                    .jsonPath("$.opening_date").isEqualTo(response.openingDate().toString())
                    .jsonPath("$.from").isEqualTo(response.from().toString())
                    .jsonPath("$.to").isEqualTo(response.to().toString())
                    .jsonPath("$.formation_date").isEqualTo(response.formationDate().toString())
                    .jsonPath("$.formation_time").isNotEmpty()
                    .jsonPath("$.balance").isEqualTo(response.balance())
                    .jsonPath("$.spent_funds").isEqualTo(response.spentFunds())
                    .jsonPath("$.received_funds").isEqualTo(response.receivedFunds());
        }

        @Test
        @DisplayName("test should return expected size and status 404 if no account found")
        void testShouldReturnExpectedSizeAndStatus404IfNoAccountFound() {
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest()
                    .withAccountId("ASSA")
                    .build();
            ExceptionResponse response = new ExceptionResponse("Account with ID " + request.accountId() + " is not found!");

            webTestClient.post()
                    .uri(TRANSACTIONS + "/amount")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ExceptionResponse.class)
                    .isEqualTo(response);
        }

    }

    @Nested
    class FindByIdGetEndpointTest {

        @Test
        @DisplayName("test should return expected json and status 200")
        void testShouldReturnExpectedJsonAndStatus200() {
            TransactionResponse response = TransactionResponseTestBuilder.aTransactionResponse().build();

            webTestClient.get()
                    .uri(TRANSACTIONS + "/" + response.id())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(TransactionResponse.class)
                    .isEqualTo(response);
        }

        @Test
        @DisplayName("test should return expected json and status 404")
        void testShouldReturnExpectedJsonAndStatus404() {
            long id = 567L;
            ExceptionResponse response = new ExceptionResponse("Transaction with ID " + id + " is not found!");

            webTestClient.get()
                    .uri(TRANSACTIONS + "/" + id)
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
                    List.of(new Violation("findById.id", "must be greater than 0")));

            webTestClient.get()
                    .uri(TRANSACTIONS + "/" + wrongId)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ValidationErrorResponse.class)
                    .isEqualTo(response);
        }

    }

    @Nested
    class FindAllBySendersAccountIdGetEndpointTest {

        @Test
        @DisplayName("test should return empty json and status 200")
        void testShouldReturnEmptyJsonAndStatus200() {
            String id = "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62";
            int offset = 40;
            int limit = 3;
            String json = "[]";

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/senders/" + id)
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
            TransactionResponse response = TransactionResponseTestBuilder.aTransactionResponse().build();

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/senders/" + response.accountSenderId())
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(TransactionResponse.class)
                    .contains(response);
        }

        @Test
        @DisplayName("test should return expected size and status 200")
        void testShouldReturnExpectedSizeAndStatus200() {
            String id = "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62";
            int offset = 0;
            int limit = 5;
            int expectedSize = 3;

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/senders/" + id)
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
            String id = "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62";
            int offset = -1;
            int limit = 5;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("offset", "must be greater than or equal to 0")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/senders/" + id)
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
            String id = "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62";
            int limit = 5;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("offset", "must not be null")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/senders/" + id)
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
            String id = "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62";
            int offset = 5;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("limit", "must not be null")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/senders/" + id)
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
            String id = "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62";
            int offset = 0;
            int limit = 0;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("limit", "must be greater than or equal to 1")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/senders/" + id)
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
            String id = "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62";
            int offset = 0;
            int limit = 21;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("limit", "must be less than or equal to 20")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/senders/" + id)
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
    class FindAllByRecipientAccountIdGetEndpointTest {

        @Test
        @DisplayName("test should return empty json and status 200")
        void testShouldReturnEmptyJsonAndStatus200() {
            String id = "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9";
            int offset = 40;
            int limit = 3;
            String json = "[]";

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/recipients/" + id)
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
            TransactionResponse response = TransactionResponseTestBuilder.aTransactionResponse().build();

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/recipients/" + response.accountRecipientId())
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(TransactionResponse.class)
                    .contains(response);
        }

        @Test
        @DisplayName("test should return expected size and status 200")
        void testShouldReturnExpectedSizeAndStatus200() {
            String id = "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9";
            int offset = 0;
            int limit = 5;
            int expectedSize = 2;

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/recipients/" + id)
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
            String id = "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9";
            int offset = -1;
            int limit = 5;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("offset", "must be greater than or equal to 0")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/recipients/" + id)
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
            String id = "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9";
            int limit = 5;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("offset", "must not be null")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/recipients/" + id)
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
            String id = "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9";
            int offset = 5;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("limit", "must not be null")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/recipients/" + id)
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
            String id = "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9";
            int offset = 0;
            int limit = 0;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("limit", "must be greater than or equal to 1")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/recipients/" + id)
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
            String id = "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9";
            int offset = 0;
            int limit = 21;
            ValidationErrorResponse response = new ValidationErrorResponse(
                    List.of(new Violation("limit", "must be less than or equal to 20")));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS + "/recipients/" + id)
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

}
