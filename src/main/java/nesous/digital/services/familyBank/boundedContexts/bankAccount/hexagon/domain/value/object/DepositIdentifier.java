package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationMessages;

import java.time.Year;
import java.util.UUID;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.utils.Guard.guard;


public record DepositIdentifier(String value) {

    public static DepositIdentifier generate() {
        String deposit = "DEP";
        String uuid = UUID.randomUUID().toString();
        return DepositIdentifier.of(uuid.substring(0, 7)+"-"+deposit+"-"+Year.now().getValue());
    }

    public static DepositIdentifier generateForASpecificYear(Year year) {
        String deposit = "DEP";
        String uuid = UUID.randomUUID().toString();
        return DepositIdentifier.of(uuid.substring(0, 7)+"-"+deposit+"-"+year.getValue());
    }

    private static DepositIdentifier of(String value) {
        guard(value).againstNullOrWhitespace(ValidationMessages.DEPOSIT_ID_EMPTY);
        return new DepositIdentifier(value);
    }
}
