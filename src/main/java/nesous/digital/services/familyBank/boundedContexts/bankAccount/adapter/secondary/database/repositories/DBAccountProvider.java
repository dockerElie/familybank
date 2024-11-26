package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.repositories;

import lombok.RequiredArgsConstructor;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.entities.AccountEntity;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.entities.DepositEntity;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.mappers.BankAccountMapper;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.mappers.DepositMapper;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.events.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.BankAccountCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;
import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.repositories.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import static nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Privilege.MANAGER;
import static org.thymeleaf.util.StringUtils.isEmptyOrWhitespace;

@Component
@RequiredArgsConstructor
public class DBAccountProvider implements AccountProvider {

    private final DomainEventPublisher eventPublisher;
    private final AccountRepository accountRepository;
    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final DepositProvider depositProvider;
    private final BankAccountMapper bankAccountMapper;
    private final DepositMapper depositMapper;

    @Override
    public Account of(AccountId accountId) {
        AccountEntity accountEntity = accountRepository.findByAccountId(accountId.accountId());
        return bankAccountMapper.fromEntityToAccount(accountEntity);
    }

    @Override
    public Account withLastDeposit(AccountId accountId) {
        List<Deposit> depositList = depositProvider.of(accountId);
        Deposit lastDeposit = depositList.get(0);
        AccountEntity accountEntity = accountRepository.findByAccountId(accountId.accountId());
        return bankAccountMapper.fromEntityToAccount(accountEntity, lastDeposit);
    }

    @Override
    public Account withDeposit(Deposit deposit) {
        AccountEntity accountEntity = accountRepository.findByAccountId(deposit.getAccountId().accountId());
        return bankAccountMapper.fromEntityToAccount(accountEntity, deposit);
    }

    @Override
    @Transactional
    public Account save(BankAccountCommand command) {
        AccountId accountId = Account.nextAccountId();
        if (command.getAccountId() != null && !isEmptyOrWhitespace(command.getAccountId().accountId())) {
            accountId = command.getAccountId();
        }
        Account account = Account.builder().accountId(accountId).accountHolder(command.getAccountHolder()).build();
        AccountEntity accountEntity = bankAccountMapper.fromAccountToEntity(account);
        accountRepository.saveAndFlush(accountEntity);
        eventPublisher.publishEvent(getAccountCreated(account));
        return account;
    }

    @Override
    public List<Account> saveAll(List<BankAccountCommand> commandList) {
        return commandList.stream()
                .map(this::save)
                .toList();
    }

    @Override
    @Transactional
    public Account activateDeposit(Account account) {
        AccountEntity accountEntity = bankAccountMapper.fromAccountToEntity(account);
        DepositEntity depositEntity = depositMapper.fromDepositToEntity(account.getDeposit());
        if (CollectionUtils.isEmpty(accountEntity.getDeposits())) {
            accountEntity.setDeposits(Collections.singletonList(depositEntity));
        } else {
            accountEntity.getDeposits().add(depositEntity);
        }
        accountRepository.saveAndFlush(accountEntity);
        eventPublisher.publishEvent(getAccountActivated(account));
        return account;
    }

    @Transactional
    @Override
    public void update(Account account) {
        AccountEntity accountEntity = accountRepository.findByAccountId(account.getAccountId().accountId());
        List<DepositEntity> depositEntities = depositRepository.findByAccountAccountIdOrderByDateDesc(
                account.getAccountId().accountId());
        List<DepositEntity> updateDepositList = depositEntities.stream().map(depositEntity -> {
            if (depositEntity.getDepositIdentifier().equals(account.getDepositIdentifier().value())) {
                if (account.getDepositMoney() != null) {
                    depositEntity.setMoney(account.getDepositMoney().value());
                }

                if (account.getDepositReminderDate() != null) {
                    depositEntity.setReminderDate(account.getDepositReminderDate().calendarDate());
                }

                if (account.getDepositUserExpirationDate() != null) {
                    depositEntity.setUserExpirationDate(account.getDepositUserExpirationDate().date().calendarDate());
                }
                if (account.getReason() != null) {
                    depositEntity.setReason(account.getReason().value());
                }
                depositEntity.setStatus(account.getDepositStatus());
                depositEntity.setDate(account.getDepositDate().calendarDate());
                depositEntity.setExpirationDate(account.getDepositExpirationDate().date().calendarDate());
            }
            return depositEntity;
        }).toList();
        accountEntity.setDeposits(updateDepositList);
        accountRepository.saveAndFlush(accountEntity);
        switch (account.getDepositStatus()) {
            case REQUESTED -> eventPublisher.publishEvent(getActivateDepositRequested());
            case REACTIVATED -> eventPublisher.publishEvent(getDepositReactivated(account));
        }
    }

    private AccountCreated getAccountCreated(Account account) {
        return AccountCreated.of(account.getAccountId(), account.getAccountHolderName(),
                UUID.randomUUID(), new Date(), account.getAccountHolderEmailAddress());
    }

    private AccountActivated getAccountActivated(Account account) {
        return AccountActivated.of(UUID.randomUUID(), new Date(), account.getAccountHolderName(),
                account.getAccountHolderEmailAddress(), account.getDepositIdentifier(), account.getDepositDate());
    }

    private ActivateDepositRequested getActivateDepositRequested() {
        String accountManagerEmail = userRepository.findByPrivilege(MANAGER).getEmail();
        return ActivateDepositRequested.of(UUID.randomUUID(), new Date(), accountManagerEmail);
    }

    private DepositReactivated getDepositReactivated(Account account) {
        return DepositReactivated.of(UUID.randomUUID(), new Date(), account.getAccountHolderName(),
                account.getAccountHolderEmailAddress(), account.getDepositIdentifier(),
                account.getDepositName().text(), account.getDepositExpirationDate().date());
    }
}
