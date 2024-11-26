package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.common;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.ExpirationDate;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.UserExpirationDate;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationException;

public class UserExpirationDateGuard extends BaseGuard<UserExpirationDate> {
    public UserExpirationDateGuard(UserExpirationDate value) {
        super(value);
    }

    public void againstUserExpirationDate(ExpirationDate expirationDate, String message) {
        if (this.value == null) {
            return;
        }
        if (value.date().isBefore(expirationDate.date())) {
            throw new ValidationException(message);
        }
    }
}
