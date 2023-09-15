package com.grigoryev.cleverbankreactiveremaster.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

public record UserRequest(
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+$",
                message = "This field must contain only letters of the Russian and English alphabets in any case")
        String lastname,

        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+$",
                message = "This field must contain only letters of the Russian and English alphabets in any case")
        String firstname,

        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+$",
                message = "This field must contain only letters of the Russian and English alphabets in any case")
        String surname,

        @JsonProperty("mobile_number")
        @Pattern(regexp = "^\\+\\d{1,3} \\(\\d{1,3}\\) \\d{3}-\\d{2}-\\d{2}$",
                message = "Please enter a valid mobile number in the format +X (XXX) XXX-XX-XX or +XXX (XX) XXX-XX-XX")
        String mobileNumber) {
}
