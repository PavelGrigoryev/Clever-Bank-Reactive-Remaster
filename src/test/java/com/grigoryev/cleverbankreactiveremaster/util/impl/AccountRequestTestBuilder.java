package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountRequest;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aAccountRequest")
@With
public class AccountRequestTestBuilder implements TestBuilder<AccountRequest> {

    private String currency = Currency.EUR.toString();
    private BigDecimal balance = BigDecimal.valueOf(10.1);
    private Long bankId = 2L;
    private Long userId = 5L;

    @Override
    public AccountRequest build() {
        return new AccountRequest(currency, balance, bankId, userId);
    }

}
