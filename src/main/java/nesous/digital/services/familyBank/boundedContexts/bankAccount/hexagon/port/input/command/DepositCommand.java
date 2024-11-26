package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command;

import lombok.Builder;
import lombok.Value;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;

@Value
@Builder
public class DepositCommand {

    DepositIdentifier depositIdentifier;
    DepositName depositName;
    Description description;
    Status status;
    Reason reason;
    Date date;
    DepositMoney depositMoney;
    ExpirationDate expirationDate;
    UserExpirationDate userExpirationDate;
}
