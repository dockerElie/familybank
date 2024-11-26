package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationMessages;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.utils.Guard.guard;

public record ExpirationDate(Date date) {

    public static ExpirationDate of(Date date) {
        guard(date).againstNull(ValidationMessages.EXPIRATION_DATE_EMPTY);
        return new ExpirationDate(date);
    }
}
