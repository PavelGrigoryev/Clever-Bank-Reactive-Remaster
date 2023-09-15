package com.grigoryev.cleverbankreactiveremaster.dto.bank;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

public record BankRequest(
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ @_-]+$",
                message = "This field must contain letters of the Russian and English alphabets in any case and @_- with space symbols")
        String name,

        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9 .,-]+$",
                message = "This field must contain letters of the Russian and English alphabets in any case and .,- with space symbols")
        String address,

        @JsonProperty("phone_number")
        @Pattern(regexp = "^\\+\\d{1,3} \\(\\d{1,3}\\) \\d{3}-\\d{2}-\\d{2}$",
                message = "Please enter a valid mobile number in the format +X (XXX) XXX-XX-XX or +XXX (XX) XXX-XX-XX")
        String phoneNumber) {
}
