package com.grigoryev.cleverbankreactiveremaster.mapper;

import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankResponse;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.Bank;
import org.mapstruct.Mapper;

@Mapper
public interface BankMapper {

    BankResponse toResponse(Bank bank);

    Bank fromRequest(BankRequest request);

}
