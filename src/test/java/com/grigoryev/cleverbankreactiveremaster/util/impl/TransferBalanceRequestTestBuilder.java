package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aTransferBalanceRequest")
@With
public class TransferBalanceRequestTestBuilder implements TestBuilder<TransferBalanceRequest> {

    private String accountSenderId = "6RE0 UZ6A 1I3X YK92 MEUR E5GX 13CW";
    private String accountRecipientId = "0UGT 45HU 37CW ZWMQ JZWK 7GLM ZBOT";
    private BigDecimal sum = new BigDecimal("20");

    @Override
    public TransferBalanceRequest build() {
        return new TransferBalanceRequest(accountSenderId, accountRecipientId, sum);
    }

}
