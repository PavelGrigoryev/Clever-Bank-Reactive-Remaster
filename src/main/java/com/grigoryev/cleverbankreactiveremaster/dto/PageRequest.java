package com.grigoryev.cleverbankreactiveremaster.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PageRequest(@NotNull
                          @PositiveOrZero
                          Integer offset,

                          @NotNull
                          @Min(value = 1)
                          @Max(value = 20)
                          Integer limit) {
}
