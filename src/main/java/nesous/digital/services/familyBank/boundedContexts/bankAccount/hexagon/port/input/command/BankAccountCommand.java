package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command;


import lombok.Builder;
import lombok.Value;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolder;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;

@Value
@Builder
public class BankAccountCommand {

    AccountId accountId;
    AccountHolder accountHolder;
    Deposit deposit;
}
