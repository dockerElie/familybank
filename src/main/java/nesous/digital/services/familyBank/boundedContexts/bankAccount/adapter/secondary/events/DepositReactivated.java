package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.events;

import lombok.Getter;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Date;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.DepositIdentifier;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;

import java.util.UUID;

@Getter
public class DepositReactivated extends DomainEvent {

    private final Text accountHolder;
    private final Text accountHolderEmailAddress;
    private final DepositIdentifier depositIdentifier;
    private final Text depositName;
    private final Date expirationDate;

    public DepositReactivated(UUID id, java.util.Date created,
                              Text accountHolder, Text accountHolderEmailAddress,
                              DepositIdentifier depositIdentifier, Text depositName, Date expirationDate) {
        super(id, created);
        this.accountHolder = accountHolder;
        this.accountHolderEmailAddress = accountHolderEmailAddress;
        this.depositIdentifier = depositIdentifier;
        this.depositName = depositName;
        this.expirationDate = expirationDate;
    }

    public static DepositReactivated of(UUID id, java.util.Date occuredOn, Text accountHolder,
                                        Text accountHolderEmailAddress, DepositIdentifier depositIdentifier,
                                        Text depositName, Date expirationDate) {

        return new DepositReactivated(id, occuredOn, accountHolder, accountHolderEmailAddress, depositIdentifier,
                depositName, expirationDate);
    }
}
