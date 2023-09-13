package com.grigoryev.cleverbankreactiveremaster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.grigoryev.cleverbankrectiveremaster.tables.pojos.Bank;
import com.grigoryev.cleverbankrectiveremaster.tables.pojos.User;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountData {

    private String id;
    private Currency currency;
    private BigDecimal balance;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private Bank bank;
    private User user;

}
