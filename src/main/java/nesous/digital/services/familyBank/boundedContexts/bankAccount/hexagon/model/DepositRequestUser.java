package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class DepositRequestUser {

    private String fullName;
    private Date depositRequestDate;
    private String depositRequestReason;
}
