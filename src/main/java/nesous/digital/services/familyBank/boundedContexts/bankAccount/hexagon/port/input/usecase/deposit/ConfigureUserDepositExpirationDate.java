package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;

import lombok.RequiredArgsConstructor;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.DepositCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.DepositException;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.CLOSED;

@RequiredArgsConstructor
public class ConfigureUserDepositExpirationDate {

    private final DepositProvider depositProvider;
    private final AccountProvider accountProvider;

    public Deposit execute(DepositCommand depositCommand) {

        Deposit deposit = depositProvider.of(depositCommand.getDepositIdentifier());
        Account account = accountProvider.of(deposit.getAccountId());

        try {
            Deposit depositWithUserExpirationDate = deposit.configureUserExpirationDate(depositCommand.getUserExpirationDate().date());
            Account updateAccount = Account.builder()
                    .accountId(deposit.getAccountId())
                    .accountHolder(account.getAccountHolder())
                    .deposit(depositWithUserExpirationDate).build();
            accountProvider.update(updateAccount);
            return depositWithUserExpirationDate;
        } catch (DepositException exception) {
            Account updateAccount = Account.builder()
                    .accountId(deposit.getAccountId())
                    .accountHolder(account.getAccountHolder())
                    .deposit(depositBuilder()
                            .withAccountId(deposit.getAccountId())
                            .withIdentifier(deposit.getIdentifier())
                            .withStatus(CLOSED)
                            .withDate(deposit.getDate())
                            .withExpirationDate(deposit.getExpirationDate())
                            .withDescription(deposit.getDescription())
                            .withName(deposit.getName())
                            .withMoney(deposit.getMoney())
                            .withReason(deposit.getFormattedReason())
                            .build()).build();
            accountProvider.update(updateAccount);
            throw new DepositException("Deposit already expired.");
        }
    }
}
