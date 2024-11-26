package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object;


import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationMessages;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;

import static nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.Guard.guard;


public record DepositName(Text text) {

    public static DepositName of(Text text) {
        guard(text).againstNullOrWhitespace(ValidationMessages.DEPOSIT_NAME_EMPTY);
        return new DepositName(text);
    }

    public String value() {
        return text().value();
    }
}
