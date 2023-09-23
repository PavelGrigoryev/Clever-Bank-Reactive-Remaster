package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.model.Type;
import com.grigoryev.cleverbankreactiveremaster.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aChangeBalanceRequest")
@With
public class ChangeBalanceRequestTestBuilder implements TestBuilder<ChangeBalanceRequest> {

    private String accountSenderId = "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62";
    private String accountRecipientId = "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9";
    private BigDecimal sum = BigDecimal.valueOf(200.52);
    private String type = Type.REPLENISHMENT.toString();

    @Override
    public ChangeBalanceRequest build() {
        return new ChangeBalanceRequest(accountSenderId, accountRecipientId, sum, type);
    }

}
