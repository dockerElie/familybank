package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;

import lombok.RequiredArgsConstructor;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.DepositIdentifier;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;

@RequiredArgsConstructor
public class CancelDeposit {

    private final DepositProvider depositProvider;
    private final AccountProvider accountProvider;
    public Deposit execute(DepositIdentifier depositIdentifier) {
        Deposit deposit = depositProvider.of(depositIdentifier);
        Account account = accountProvider.of(deposit.getAccountId());
        Deposit depositCancel = deposit.cancel();
        Account updateAccount = Account.builder()
                .accountId(deposit.getAccountId())
                .accountHolder(account.getAccountHolder())
                .deposit(depositCancel).build();
        accountProvider.update(updateAccount);
        return depositCancel;
    }
}
