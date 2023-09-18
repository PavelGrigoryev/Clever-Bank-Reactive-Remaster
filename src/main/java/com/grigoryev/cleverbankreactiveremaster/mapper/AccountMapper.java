package com.grigoryev.cleverbankreactiveremaster.mapper;

import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountResponse;
import com.grigoryev.cleverbankreactiveremaster.model.AccountData;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AccountMapper {

    AccountResponse toResponse(AccountData accountData);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bankId", source = "bank.id")
    Account fromAccountData(AccountData accountData);

    Account fromRequest(AccountRequest request);

}
