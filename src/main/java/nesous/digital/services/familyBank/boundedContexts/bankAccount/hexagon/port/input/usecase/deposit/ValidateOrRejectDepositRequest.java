package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;

import lombok.RequiredArgsConstructor;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.model.DepositRequestUser;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.DepositCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;

import java.util.Date;
import java.util.List;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.REQUESTED;

@RequiredArgsConstructor
public class ValidateOrRejectDepositRequest {

    private final DepositProvider depositProvider;
    private final AccountProvider accountProvider;
    public Deposit accept(DepositCommand depositCommand) {

        Deposit existingDeposit = depositProvider.of(depositCommand.getDepositIdentifier());
        Account account = accountProvider.of(existingDeposit.getAccountId());
        Deposit depositReactivated = existingDeposit.accept(depositCommand.getExpirationDate());
        Account updateAccount = Account.builder()
                .accountId(depositReactivated.getAccountId())
                .accountHolder(account.getAccountHolder())
                .deposit(depositReactivated).build();
        accountProvider.update(updateAccount);
        return depositReactivated;
    }

    public Deposit reject(DepositCommand depositCommand) {

        Deposit existingDeposit = depositProvider.of(depositCommand.getDepositIdentifier());
        Account account = accountProvider.of(existingDeposit.getAccountId());
        Deposit depositReactivated = existingDeposit.reject();
        Account updateAccount = Account.builder()
                .accountId(depositReactivated.getAccountId())
                .accountHolder(account.getAccountHolder())
                .deposit(depositReactivated).build();
        accountProvider.update(updateAccount);
        return depositReactivated;
    }

    public List<DepositRequestUser> usersMadeDepositRequest() {
        List<Deposit> depositList = depositProvider.searchDepositByStatus(REQUESTED);

        return depositList.stream()
                .map(deposit -> {
                    Account account = accountProvider.of(deposit.getAccountId());
                    String fullName = account.getAccountHolderName().value();
                    Date requestActivateDate = account.getDepositExpirationDate().date().calendarDate();
                    String depositRequestReason = deposit.getFormattedReason().value();
                    return DepositRequestUser.builder()
                            .fullName(fullName)
                            .depositRequestDate(requestActivateDate)
                            .depositRequestReason(depositRequestReason)
                            .build();
                }).toList();
    }

}
