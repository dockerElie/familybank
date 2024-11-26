package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class DomainEvent {

    private UUID id;
    private Date created;
}
