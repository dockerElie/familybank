package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;

import lombok.RequiredArgsConstructor;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.DepositIdentifier;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.DepositException;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.CLOSED;

@RequiredArgsConstructor
public class ValidateDeposit {

    private final DepositProvider depositProvider;
    private final AccountProvider accountProvider;
    public Deposit execute(DepositIdentifier depositIdentifier) {

        Deposit deposit = depositProvider.of(depositIdentifier);
        Account account = accountProvider.of(deposit.getAccountId());
        try {
            Deposit validatedDeposit = deposit.validate();

            Account updateAccount = Account.builder()
                    .accountId(deposit.getAccountId())
                    .accountHolder(account.getAccountHolder())
                    .deposit(validatedDeposit).build();
            accountProvider.update(updateAccount);
            return validatedDeposit;
        } catch (DepositException exception) {
            Account updateAccount = Account.builder()
                    .accountId(deposit.getAccountId())
                    .accountHolder(account.getAccountHolder())
                    .deposit(depositBuilder()
                            .withAccountId(deposit.getAccountId())
                            .withIdentifier(deposit.getIdentifier())
                            .withStatus(CLOSED)
                            .withDate(deposit.getDate())
                            .withMoney(deposit.getMoney())
                            .withExpirationDate(deposit.getExpirationDate())
                            .withUserExpirationDate(deposit.getUserExpirationDate())
                            .withDescription(deposit.getDescription())
                            .withName(deposit.getName())
                            .withReason(deposit.getFormattedReason())
                            .build()).build();
            accountProvider.update(updateAccount);
            throw new DepositException("Deposit already expired.");
        }

    }
}
