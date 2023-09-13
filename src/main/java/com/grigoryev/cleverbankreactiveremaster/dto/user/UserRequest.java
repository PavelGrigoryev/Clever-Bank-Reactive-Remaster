package com.grigoryev.cleverbankreactiveremaster.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserRequest(String lastname,
                          String firstname,
                          String surname,

                          @JsonProperty("mobile_number")
                          String mobileNumber) {
}
