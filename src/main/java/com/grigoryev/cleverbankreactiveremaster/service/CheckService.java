package com.grigoryev.cleverbankreactiveremaster.service;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.AmountStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ExchangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceResponse;

public interface CheckService {

    String createChangeBalanceCheck(ChangeBalanceResponse response);

    String createTransferBalanceCheck(TransferBalanceResponse response);

    String createExchangeBalanceCheck(ExchangeBalanceResponse response);

    String createTransactionStatement(TransactionStatementResponse response);

    String createAmountStatement(AmountStatementResponse response);

}
