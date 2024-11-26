package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.events;

import lombok.Getter;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Date;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.DepositIdentifier;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;


import java.util.UUID;

@Getter
public class AccountActivated extends DomainEvent {

    private final Text accountHolder;
    private final Text accountHolderEmailAddress;
    private final DepositIdentifier depositIdentifier;
    private final Date expirationDate;

    private AccountActivated(UUID id, java.util.Date created, Text accountHolder, Text accountHolderEmailAddress,
                             DepositIdentifier depositIdentifier,
                             Date expirationDate) {
        super(id, created);
        this.accountHolder = accountHolder;
        this.accountHolderEmailAddress = accountHolderEmailAddress;
        this.depositIdentifier = depositIdentifier;
        this.expirationDate = expirationDate;
    }

    public static AccountActivated of(UUID id, java.util.Date occuredOn, Text accountHolder,
                                      Text accountHolderEmailAddress, DepositIdentifier depositIdentifier,
                                      Date expirationDate) {

        return new AccountActivated(id, occuredOn, accountHolder, accountHolderEmailAddress, depositIdentifier,
                expirationDate);
    }
}
