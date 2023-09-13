package com.grigoryev.cleverbankreactiveremaster.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserResponse(Long id,
                           String lastname,
                           String firstname,
                           String surname,
                           LocalDate registerDate,
                           String mobileNumber) {
}
