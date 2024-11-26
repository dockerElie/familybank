package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.common;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Reason;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationException;

public class ReasonGuard extends BaseGuard<Reason>{
    public ReasonGuard(Reason value) {
        super(value);
    }

    public void againstReason(Reason reason, String message) {

        if (reason.text().isNull()) {
            throw new ValidationException(message);
        }
    }
}
