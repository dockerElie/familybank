package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.model;

import lombok.Builder;
import lombok.Getter;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Date;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status;

@Builder
@Getter
public class DepositFilter {

    private String searchByIdentifier;
    private Status searchByStatus;
    private Date searchByStartDate;
    private Date searchByEndDate;
}
