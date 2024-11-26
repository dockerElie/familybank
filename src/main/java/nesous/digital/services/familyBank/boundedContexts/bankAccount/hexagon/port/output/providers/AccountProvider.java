package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.BankAccountCommand;

import java.util.List;

public interface AccountProvider {

    Account of(AccountId accountId);

    Account withLastDeposit(AccountId accountId);

    Account withDeposit(Deposit deposit);

    Account save(BankAccountCommand command);

    List<Account> saveAll(List<BankAccountCommand> commandList);

    Account activateDeposit(Account account);

    void update(Account account);
}
