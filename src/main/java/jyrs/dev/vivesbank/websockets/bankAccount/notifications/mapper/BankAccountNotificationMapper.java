package jyrs.dev.vivesbank.websockets.bankAccount.notifications.mapper;

import jyrs.dev.vivesbank.products.bankAccounts.mappers.BankAccountMapper;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto.BankAccountNotificationResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BankAccountNotificationMapper {

    private BankAccountMapper bankAccountMapper;
    public BankAccountNotificationResponse toNotificationResponse(BankAccount account) {
        if (account == null) {
            return null;
        }

        return new BankAccountNotificationResponse(
                account.getId(),
                account.getIban(),
                account.getAccountType().toString(),
                account.getBalance(),
                bankAccountMapper.toCardDto(account.getCreditCard()),
                LocalDateTime.now().toString()
        );
    }
}