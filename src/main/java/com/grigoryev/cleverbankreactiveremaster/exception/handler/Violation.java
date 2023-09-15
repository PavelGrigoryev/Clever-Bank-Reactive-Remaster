package com.grigoryev.cleverbankreactiveremaster.exception.handler;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Violation(String fieldName,
                        String message) {
}
