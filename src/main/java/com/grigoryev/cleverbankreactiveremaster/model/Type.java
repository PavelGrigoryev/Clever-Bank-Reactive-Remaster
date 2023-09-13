package com.grigoryev.cleverbankreactiveremaster.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Type {

    REPLENISHMENT("Пополнение"),
    WITHDRAWAL("Снятие"),
    TRANSFER("Перевод");

    private final String name;

}
