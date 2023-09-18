package com.grigoryev.cleverbankreactiveremaster.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {

    BYN(0), RUB(456), USD(431), EUR(451);

    private final Integer code;

}
