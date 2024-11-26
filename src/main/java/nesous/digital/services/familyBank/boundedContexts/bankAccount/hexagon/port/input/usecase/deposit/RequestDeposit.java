package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;

import lombok.RequiredArgsConstructor;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.DepositCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;

@RequiredArgsConstructor
public class RequestDeposit {

    private final DepositProvider depositProvider;
    private final AccountProvider accountProvider;

    public Deposit execute(DepositCommand depositCommand) {

        Deposit deposit = depositProvider.of(depositCommand.getDepositIdentifier());
        Account account = accountProvider.of(deposit.getAccountId());
        Deposit depositRequest = deposit.openARequest();
        Account updateAccount = Account.builder()
                .accountId(deposit.getAccountId())
                .accountHolder(account.getAccountHolder())
                .deposit(depositRequest).build();
        accountProvider.update(updateAccount);
        return depositRequest;
    }
}
