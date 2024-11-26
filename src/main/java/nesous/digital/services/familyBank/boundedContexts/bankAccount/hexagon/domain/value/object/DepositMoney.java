package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object;


import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationMessages;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.utils.Guard.guard;

public record DepositMoney(Money money) {

    public static DepositMoney of(Money money) {
        if (money == null) {
            return new DepositMoney(Money.ZERO);
        }
        guard(money).againstNegative(ValidationMessages.BALANCE_NEGATIVE);
        return new DepositMoney(money);
    }

    public double value() {
        return money().value();
    }
}
