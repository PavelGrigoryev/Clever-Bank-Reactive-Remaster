package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankRequest;
import com.grigoryev.cleverbankreactiveremaster.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aBankRequest")
@With
public class BankRequestTestBuilder implements TestBuilder<BankRequest> {

    private String name = "Супер-Банк";
    private String address = "ул. Орловская, 77";
    private String phoneNumber = "+7 (495) 456-13-11";

    @Override
    public BankRequest build() {
        return new BankRequest(name, address, phoneNumber);
    }

}
