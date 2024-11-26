package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.mappers;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.entities.AccountEntity;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolder;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolderIdentifier;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.entities.UserEntity;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.output.providers.UserProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class BankAccountMapper {

    private final UserProvider userProvider;

    public BankAccountMapper(@Qualifier("dbUserProvider") UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    public AccountEntity fromAccountToEntity(Account account) {

        UserEntity userEntity = userProvider.getEntityOf(account.getAccountHolder().identifier().value());
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccountId(account.getAccountId().accountId());
        accountEntity.setUserId(userEntity);

        return accountEntity;
    }

    public Account fromEntityToAccount(AccountEntity accountEntity, Deposit deposit) {
        UserEntity user = accountEntity.getUserId();
        AccountHolderIdentifier accountHolderIdentifier = new AccountHolderIdentifier(
                accountEntity.getAccountId());
        AccountId accountId = AccountId.of(accountEntity.getAccountId());
        Text email = new Text(user.getEmail());
        Text lastName = new Text(user.getLastName());
        Text firstName = new Text(user.getFirstName());

        AccountHolder accountHolder = AccountHolder.of(accountHolderIdentifier,
                lastName, firstName, email);
        return Account.builder()
                .accountId(accountId)
                .accountHolder(accountHolder)
                .deposit(deposit)
                .build();
    }

    public Account fromEntityToAccount(AccountEntity accountEntity) {
        UserEntity user = accountEntity.getUserId();
        AccountHolderIdentifier accountHolderIdentifier = new AccountHolderIdentifier(
                accountEntity.getAccountId());
        AccountId accountId = AccountId.of(accountEntity.getAccountId());
        Text email = new Text(user.getEmail());
        Text lastName = new Text(user.getLastName());
        Text firstName = new Text(user.getFirstName());

        AccountHolder accountHolder = AccountHolder.of(accountHolderIdentifier,
                lastName, firstName, email);
        return Account.builder()
                .accountId(accountId)
                .accountHolder(accountHolder)
                .build();
    }
}
