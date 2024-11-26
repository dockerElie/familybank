package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.repositories;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.entities.DepositEntity;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status;

import java.util.List;

public interface DepositRepository extends ReadOnlyRepository<DepositEntity, Long> {

    List<DepositEntity> findByAccountAccountIdOrderByDateDesc(String accountId);

    List<DepositEntity> findByAccountAccountIdOrderByUserExpirationDateDesc(String accountId);

    DepositEntity findByDepositIdentifier(String id);

    List<DepositEntity> findByStatus(Status status);
}
