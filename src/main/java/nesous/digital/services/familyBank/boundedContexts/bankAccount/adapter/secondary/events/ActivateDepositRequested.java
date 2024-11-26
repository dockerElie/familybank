package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.events;


import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
public class ActivateDepositRequested extends DomainEvent {

    private final String managerEmail;
    public ActivateDepositRequested(UUID id, Date created, String managerEmail) {
        super(id, created);
        this.managerEmail = managerEmail;
    }

    public static ActivateDepositRequested of(UUID id, java.util.Date occuredOn, String managerEmail) {

        return new ActivateDepositRequested(id, occuredOn, managerEmail);
    }
}
