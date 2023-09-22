package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankResponse;
import com.grigoryev.cleverbankreactiveremaster.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aBankResponse")
@With
public class BankResponseTestBuilder implements TestBuilder<BankResponse> {

    private Long id = 1L;
    private String name = "Клевер-Банк";
    private String address = "ул. Тверская, 25";
    private String phoneNumber = "+7 (495) 222-22-22";

    @Override
    public BankResponse build() {
        return new BankResponse(id, name, address, phoneNumber);
    }

}
