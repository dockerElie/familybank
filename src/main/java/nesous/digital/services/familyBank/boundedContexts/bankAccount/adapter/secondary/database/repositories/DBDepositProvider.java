package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.repositories;

import lombok.RequiredArgsConstructor;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.entities.DepositEntity;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.mappers.DepositMapper;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.DepositIdentifier;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DBDepositProvider implements DepositProvider {

    private final DepositRepository depositRepository;
    private final DepositMapper depositMapper;
    @Override
    public List<Deposit> of(AccountId accountId) {
        List<DepositEntity> depositEntities = depositRepository.findByAccountAccountIdOrderByDateDesc(accountId.accountId());
        return depositMapper.fromEntityToDeposit(depositEntities);
    }

    @Override
    public Deposit of(DepositIdentifier depositIdentifier) {
        DepositEntity depositEntity = depositRepository.findByDepositIdentifier(depositIdentifier.value());
        return depositMapper.fromEntityToDeposit(depositEntity);
    }

    @Override
    public List<Deposit> searchDepositByStatus(Status status) {
        List<DepositEntity> depositEntities = depositRepository.findByStatus(status);
        return depositMapper.fromEntityToDeposit(depositEntities);
    }

    @Override
    public Deposit searchDepositByMaximumUserExpirationDate(AccountId accountId) {

        List<DepositEntity> depositEntities = depositRepository
                .findByAccountAccountIdOrderByUserExpirationDateDesc(accountId.accountId());

        return depositMapper.fromEntityToDeposit(depositEntities).get(0);
    }

    @Override
    public List<Deposit> list() {
        List<DepositEntity> depositEntities = depositRepository.findAll();
        return depositMapper.fromEntityToDeposit(depositEntities);
    }
}
