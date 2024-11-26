package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.common;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Date;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.ExpirationDate;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationException;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationMessages.DEPOSIT_DATE_EMPTY;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationMessages.EXPIRATION_DATE_EMPTY;

public class ExpirationDateGuard extends BaseGuard<ExpirationDate> {
    public ExpirationDateGuard(ExpirationDate value) {
        super(value);
    }

    public void againstExpirationDate(Date depositDate, String message) {
        if (this.value == null) {
            throw new ValidationException(EXPIRATION_DATE_EMPTY);
        }
        if (value.date().isBefore(depositDate)) {
            throw new ValidationException(message);
        }
    }
}
