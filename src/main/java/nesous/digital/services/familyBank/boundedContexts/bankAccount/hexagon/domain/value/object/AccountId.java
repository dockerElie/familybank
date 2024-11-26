package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationMessages;

import static nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.Guard.guard;

public record AccountId(String accountId) {

    public static AccountId of(String value) {
        guard(value).againstNullOrWhitespace(ValidationMessages.ACCOUNT_ID_EMPTY);
        return new AccountId(value);
    }
}
