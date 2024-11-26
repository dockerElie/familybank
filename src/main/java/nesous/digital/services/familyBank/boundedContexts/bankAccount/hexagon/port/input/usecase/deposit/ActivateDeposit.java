package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;

import lombok.RequiredArgsConstructor;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.logging.LogEntry;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.logging.LoggingProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.DepositCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationException;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.bankAccount.CreateBankAccount;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationMessages.INVALID_ACCOUNT;

@RequiredArgsConstructor
public class ActivateDeposit {

    private final AccountProvider accountProvider;
    private final LoggingProvider loggerProvider;
    public Account execute(AccountId accountId, DepositCommand command) {

        Account account = accountProvider.withLastDeposit(accountId);

        if (account == null) {
            throw new ValidationException(INVALID_ACCOUNT);
        }

        Deposit newDeposit = depositBuilder()
                .withName(command.getDepositName())
                .withDate(command.getDate())
                .withDescription(command.getDescription())
                .withExpirationDate(command.getExpirationDate())
                .build();

        Account accountActivated = account.activateDeposit(newDeposit);
        LogEntry logEntry = new LogEntry(LogEntry.Level.INFO,
                String.format("Updating Deposit bank account for %s with following deposit details, ID: %s - NAME: %s",
                        account.getAccountHolderName().value(),
                        accountActivated.getDepositIdentifier().value(),
                        accountActivated.getDepositName().text()),
                null, CreateBankAccount.class);
        loggerProvider.log(logEntry);
        return accountProvider.activateDeposit(accountActivated);
    }
}
