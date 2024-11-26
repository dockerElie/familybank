package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.DepositIdentifier;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status;

import java.util.List;

public interface DepositProvider {

    List<Deposit> of(AccountId accountId);

    Deposit of(DepositIdentifier depositIdentifier);

    List<Deposit> searchDepositByStatus(Status status);

    Deposit searchDepositByMaximumUserExpirationDate(AccountId accountId);

    List<Deposit> list();
}
