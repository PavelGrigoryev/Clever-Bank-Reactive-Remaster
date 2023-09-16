package com.grigoryev.cleverbankreactiveremaster.job;

import com.grigoryev.cleverbankreactiveremaster.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public final class MonthPercentageScheduler {

    private final AccountService accountService;
    private final TransactionalOperator operator;

    @Value("${scheduler.monthPercentage}")
    private String monthPercentage;

    @Scheduled(initialDelayString = "${scheduler.initialDelay}", fixedRateString = "${scheduler.period}")
    private void chargePercentage() {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalTime lowerBound = LocalTime.of(23, 59, 29);
        LocalTime upperBound = LocalTime.of(23, 59, 59);
        boolean isLastDayOfMonth = currentDate.getDayOfMonth() == currentDate.lengthOfMonth();
        boolean isInInterval = currentTime.isAfter(lowerBound) && currentTime.isBefore(upperBound);
        if (isLastDayOfMonth && isInInterval) {
            accountService.findAll()
                    .filter(account -> account.getClosingDate() == null)
                    .flatMap(account -> accountService.updateBalance(account,
                            account.getBalance().multiply(new BigDecimal(monthPercentage))
                                    .multiply(BigDecimal.valueOf(0.01))
                                    .setScale(2, RoundingMode.DOWN)
                                    .add(account.getBalance())).log())
                    .as(operator::transactional)
                    .subscribe();
        }
    }

}
