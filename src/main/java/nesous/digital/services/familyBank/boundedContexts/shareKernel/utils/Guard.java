package nesous.digital.services.familyBank.boundedContexts.shareKernel.utils;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.common.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.ExpirationDate;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Money;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.UserExpirationDate;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Email;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.commons.EmailGuard;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.commons.TextGuard;

public class Guard {

    private Guard(){}

    public static LongGuard guard(long value) {
        return new LongGuard(value);
    }

    public static MoneyGuard guard(Money value) {
        return new MoneyGuard(value);
    }

    public static ObjectGuard guard(Object value) { return new ObjectGuard(value); }

    public static TextGuard guard(Text value) {
        return new TextGuard(value);
    }

    public static EmailGuard guard(Email value) {
        return new EmailGuard(value);
    }
    public static TextGuard guard(String value) {
        return new TextGuard(new Text(value));
    }

    public static UserExpirationDateGuard guard(UserExpirationDate value) { return new UserExpirationDateGuard(value);}

    public static ExpirationDateGuard guard(ExpirationDate value) {
        return new ExpirationDateGuard(value);
    }
}
