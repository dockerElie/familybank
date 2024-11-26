package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.bankAccount;

import lombok.RequiredArgsConstructor;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.logging.LogEntry;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.logging.LoggingProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.BankAccountCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;

import java.util.List;

@RequiredArgsConstructor
public class CreateBankAccount {

    private final AccountProvider accountProvider;
    private final LoggingProvider loggerProvider;

    public Account execute(BankAccountCommand command) {
        LogEntry logEntry = new LogEntry(LogEntry.Level.INFO, "Creating Deposit bank account for " +
                command.getAccountHolder().getFullName().value(), null, CreateBankAccount.class);
        loggerProvider.log(logEntry);
        return accountProvider.save(command);
    }

    public List<Account> execute(List<BankAccountCommand> commandList) {
        LogEntry logEntry = new LogEntry(LogEntry.Level.INFO, "Creating Deposit bank account size: " +
                commandList.size(), null, CreateBankAccount.class);
        loggerProvider.log(logEntry);
        return accountProvider.saveAll(commandList);
    }
}
