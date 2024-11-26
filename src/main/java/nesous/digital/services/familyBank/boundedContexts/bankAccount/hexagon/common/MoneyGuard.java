package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.common;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Money;

public class MoneyGuard extends BaseGuard<Money> {

    public MoneyGuard(Money value) {
        super(value);
    }

    public void againstNegative(String message) {
        against(value::isNegative, message);
    }
}
