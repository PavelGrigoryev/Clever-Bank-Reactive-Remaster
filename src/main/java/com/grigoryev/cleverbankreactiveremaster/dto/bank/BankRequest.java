package com.grigoryev.cleverbankreactiveremaster.dto.bank;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BankRequest(String name,
                          String address,

                          @JsonProperty("phone_number")
                          String phoneNumber) {
}
