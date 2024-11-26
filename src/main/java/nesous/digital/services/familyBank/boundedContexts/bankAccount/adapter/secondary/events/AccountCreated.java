package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.events;

import lombok.Getter;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;

import java.util.Date;
import java.util.UUID;

@Getter
public class AccountCreated extends DomainEvent {

    private final AccountId accountId;
    private final Text accountHolder;
    private final Text accountHolderEmailAddress;

    private AccountCreated(AccountId accountId, Text accountHolder, UUID id, Date occuredOn, Text accountHolderEmailAddress) {
        super(id, occuredOn);
        this.accountId = accountId;
        this.accountHolder = accountHolder;
        this.accountHolderEmailAddress = accountHolderEmailAddress;
    }

    public static AccountCreated of(AccountId accountId, Text accountHolder, UUID id, Date occuredOn, Text accountHolderEmailAddress) {
        return new AccountCreated(accountId, accountHolder,id, occuredOn, accountHolderEmailAddress);
    }
}
