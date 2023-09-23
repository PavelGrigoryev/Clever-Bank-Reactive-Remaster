package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.user.UserResponse;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aAccountResponse")
@With
public class AccountResponseTestBuilder implements TestBuilder<AccountResponse> {

    private String id = "AS12 ASDG 1200 2132 ASDA 353A 2132";
    private Currency currency = Currency.RUB;
    private BigDecimal balance = new BigDecimal("10000.00");
    private LocalDate openingDate = LocalDate.of(2020, Month.JANUARY, 1);
    private LocalDate closingDate = null;
    private BankResponse bank = BankResponseTestBuilder.aBankResponse().build();
    private UserResponse user = UserResponseTestBuilder.aUserResponse().build();

    @Override
    public AccountResponse build() {
        return new AccountResponse(id, currency, balance, openingDate, closingDate, bank, user);
    }

}
