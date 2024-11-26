package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationMessages;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.utils.Guard.guard;

public record Money(double value) {

    public static final Money ZERO = Money.of(0);

    public Money add(Money other) {
        return Money.of(value + other.value());
    }

    public boolean lessThan(Money other) {
        return value < other.value();
    }

    public boolean isNegative() {
        return lessThan(ZERO);
    }

    public static Money of(double value) {
        guard(value).againstNull(ValidationMessages.BALANCE_EMPTY);
        return new Money(value);
    }
}
