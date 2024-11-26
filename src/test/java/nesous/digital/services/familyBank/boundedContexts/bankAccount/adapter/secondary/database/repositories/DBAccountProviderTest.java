package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.repositories;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.entities.AccountEntity;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.entities.DepositEntity;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.mappers.BankAccountMapper;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.mappers.DepositMapper;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.events.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.BankAccountCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.entities.UserEntity;
import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.repositories.UserRepository;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.*;
import static nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Privilege.MANAGER;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class DBAccountProviderTest {

    DomainEventPublisher eventPublisherMock = createMock(DomainEventPublisher.class);
    AccountRepository accountRepositoryMock = createMock(AccountRepository.class);

    UserRepository userRepositoryMock = createMock(UserRepository.class);

    DepositProvider depositProviderMock = createMock(DepositProvider.class);

    DepositRepository depositRepositoryMock = createMock(DepositRepository.class);

    BankAccountMapper bankAccountMapperMock = createMock(BankAccountMapper.class);

    DepositMapper depositMapperMock = createMock(DepositMapper.class);

    private DBAccountProvider dbAccountProvider;

    private static Stream<Arguments> testSave_GivenBankAccountCommand_thenSaveToDB() {

        String userId = "1L";
        Text lastName  = new Text("ELIE MICHEL");
        Text firstName = new Text("NONO");
        Text email = new Text("eliewear1@gmail.com");

        AccountId accountId = Account.nextAccountId();
        AccountHolder accountHolder = AccountHolder.of(new AccountHolderIdentifier(userId), lastName, firstName, email);
        BankAccountCommand command = BankAccountCommand.builder()
                .accountId(accountId)
                .accountHolder(accountHolder)
                .build();
        return Stream.of(Arguments.of(command));
    }

    private static Stream<Arguments> testSaveAll_GivenListOfBankAccountCommand_thenSave() {

        String userId = "1L";
        Text lastName  = new Text("ELIE MICHEL");
        Text firstName = new Text("NONO");
        Text email = new Text("eliewear1@gmail.com");

        AccountId accountId = Account.nextAccountId();
        AccountHolder accountHolder = AccountHolder.of(new AccountHolderIdentifier(userId), lastName, firstName, email);
        BankAccountCommand command = BankAccountCommand.builder()
                .accountId(accountId)
                .accountHolder(accountHolder)
                .build();
        return Stream.of(Arguments.of(Collections.singletonList(command)));
    }

    private static Stream<Arguments> testUpdate_givenAccount_thenUpdate() {

        String userId = "1L";
        Text lastName  = new Text("ELIE MICHEL");
        Text firstName = new Text("NONO");
        Text email = new Text("eliewear1@gmail.com");

        Deposit deposit = depositBuilder()
                .withIdentifier(DepositIdentifier.generate())
                .withName(DepositName.of(new Text("deposit name")))
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withExpirationDate(ExpirationDate.of(new Date(5, Month.JANUARY, 2024)))
                .withDepositReminderDate(new DepositReminderDate(new Date(8, Month.JANUARY, 2023)))
                .withDescription(new Description(new Text("deposit name")))
                .withMoney(DepositMoney.of(Money.of(100)))
                .withStatus(ACTIVATED)
                .build();

        AccountId accountId = Account.nextAccountId();
        AccountHolder accountHolder = AccountHolder.of(new AccountHolderIdentifier(userId), lastName, firstName, email);
        Account account = Account.builder()
                .accountId(accountId)
                .accountHolder(accountHolder)
                .deposit(deposit)
                .build();
        return Stream.of(Arguments.of(account));
    }

    private static Stream<Arguments> testActivateDeposit_GivenBankAccountCommandWithDepositDetails_thenActivate() {

        String userId = "1L";
        Text lastName  = new Text("ELIE MICHEL");
        Text firstName = new Text("NONO");
        Text email = new Text("eliewear1@gmail.com");

        Deposit deposit = depositBuilder()
                .withIdentifier(DepositIdentifier.generate())
                .withName(DepositName.of(new Text("deposit name")))
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withExpirationDate(ExpirationDate.of(new Date(5, Month.JANUARY, 2024)))
                .withDepositReminderDate(new DepositReminderDate(new Date(8, Month.JANUARY, 2023)))
                .withDescription(new Description(new Text("deposit name")))
                .withMoney(DepositMoney.of(Money.of(100)))
                .withStatus(ACTIVATED)
                .build();

        AccountId accountId = Account.nextAccountId();
        AccountHolder accountHolder = AccountHolder.of(new AccountHolderIdentifier(userId), lastName, firstName, email);
        Account account = Account.builder()
                .accountId(accountId)
                .accountHolder(accountHolder)
                .deposit(deposit)
                .build();
        return Stream.of(Arguments.of(account));

    }

    @BeforeEach
    public void setup() {
        dbAccountProvider = new DBAccountProvider(eventPublisherMock,
                accountRepositoryMock, depositRepositoryMock, userRepositoryMock, depositProviderMock,
                bankAccountMapperMock, depositMapperMock);
    }

    @Test
    public void withLastDeposit_givenAccountId_ThenReturnAccountWithLastDeposit() {
        // Arrange
        Deposit deposit = depositBuilder().build();
        AccountEntity accountEntity = new AccountEntity();
        Account account = Account.builder().build();
        expect(depositProviderMock.of(isA(AccountId.class))).andReturn(Collections.singletonList(deposit));
        expect(accountRepositoryMock.findByAccountId(isA(String.class))).andReturn(accountEntity);
        expect(bankAccountMapperMock.fromEntityToAccount(isA(AccountEntity.class), isA(Deposit.class)))
                .andReturn(account);

        // Act
        replay(depositProviderMock, accountRepositoryMock, bankAccountMapperMock);
        Account result = dbAccountProvider.withLastDeposit(Account.nextAccountId());

        // Assert
        assertNotNull(result);
    }

    @Test
    public void withDeposit_givenAccountId_ThenReturnAccountWithDeposit() {
        // Arrange
        Deposit deposit = depositBuilder().withAccountId(Account.nextAccountId()).build();
        AccountEntity accountEntity = new AccountEntity();
        Account account = Account.builder().build();
        expect(accountRepositoryMock.findByAccountId(isA(String.class))).andReturn(accountEntity);
        expect(bankAccountMapperMock.fromEntityToAccount(isA(AccountEntity.class), isA(Deposit.class)))
                .andReturn(account);

        // Act
        replay(accountRepositoryMock, bankAccountMapperMock);
        Account result = dbAccountProvider.withDeposit(deposit);

        // Assert
        assertNotNull(result);
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("Save account to DB and publish account created event")
    public void testSave_GivenBankAccountCommand_thenSaveToDB(BankAccountCommand command) {

        // Arrange
        Capture<AccountCreated> eventCapture = newCapture();
        AccountEntity accountEntity = new AccountEntity();
        eventPublisherMock.publishEvent(capture(eventCapture));
        EasyMock.expectLastCall().andVoid().anyTimes();

        expect(bankAccountMapperMock.fromAccountToEntity(isA(Account.class))).andReturn(accountEntity);
        expect(accountRepositoryMock.saveAndFlush(accountEntity)).andReturn(accountEntity);

        // Act
        replay(eventPublisherMock, bankAccountMapperMock, accountRepositoryMock);
        Account account = dbAccountProvider.save(command);
        AccountCreated capturedAccountCreatedEvent = eventCapture.getValue();

        // Assert
        assertEquals(account.getAccountId(), capturedAccountCreatedEvent.getAccountId());
        assertNotNull(capturedAccountCreatedEvent.getId());

        // Verify the mock
        EasyMock.verify(eventPublisherMock, bankAccountMapperMock, accountRepositoryMock);

    }
    @ParameterizedTest
    @MethodSource
    @DisplayName("Activate a deposit to DB and publish activated deposit event")
    public void testActivateDeposit_GivenBankAccountCommandWithDepositDetails_thenActivate(Account account) {

        // Arrange
        AccountEntity accountEntity = new AccountEntity();
        DepositEntity depositEntity = new DepositEntity();

        Capture<AccountActivated> eventCapture = newCapture();
        expect(bankAccountMapperMock.fromAccountToEntity(account)).andReturn(accountEntity);
        expect(depositMapperMock.fromDepositToEntity(account.getDeposit())).andReturn(depositEntity);
        expect(accountRepositoryMock.saveAndFlush(isA(AccountEntity.class))).andReturn(accountEntity);
        eventPublisherMock.publishEvent(capture(eventCapture));
        EasyMock.expectLastCall().andVoid().anyTimes();

        // Act
        replay(eventPublisherMock, bankAccountMapperMock, depositMapperMock, accountRepositoryMock);
        Account accountActivated = dbAccountProvider.activateDeposit(account);
        AccountActivated capturedDepositActivatedEvent = eventCapture.getValue();

        // Assert
        assertNotNull(capturedDepositActivatedEvent.getId());
        assertNotNull(accountActivated.getDepositIdentifier());

        assertEquals(accountActivated.getAccountHolderName(), capturedDepositActivatedEvent.getAccountHolder());

        assertEquals(accountActivated.getDepositIdentifier(),
                capturedDepositActivatedEvent.getDepositIdentifier());

        assertEquals("deposit name", accountActivated.getDepositName().text().value());
        assertEquals(ACTIVATED, accountActivated.getDepositStatus());
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("Save list of Bank account")
    public void testSaveAll_GivenListOfBankAccountCommand_thenSave(
            List<BankAccountCommand> bankAccountCommandList) {
        // Arrange
        Capture<AccountCreated> eventCapture = newCapture();
        AccountEntity accountEntity = new AccountEntity();
        eventPublisherMock.publishEvent(capture(eventCapture));
        EasyMock.expectLastCall().andVoid().anyTimes();

        expect(bankAccountMapperMock.fromAccountToEntity(isA(Account.class))).andReturn(accountEntity);
        expect(accountRepositoryMock.saveAndFlush(accountEntity)).andReturn(accountEntity);

        // Act
        replay(eventPublisherMock, bankAccountMapperMock, accountRepositoryMock);
        List<Account> accounts = dbAccountProvider.saveAll(bankAccountCommandList);
        AccountCreated capturedAccountCreatedEvent = eventCapture.getValue();

        // Assert
        assertEquals(accounts.get(0).getAccountId(), capturedAccountCreatedEvent.getAccountId());
        assertNotNull(capturedAccountCreatedEvent.getId());

        // Verify the mock
        verify(eventPublisherMock, bankAccountMapperMock, accountRepositoryMock);
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("update deposit")
    public void testUpdate_givenAccount_thenUpdate(Account account) {

        // Arrange
        Capture<AccountEntity> eventCapture = newCapture();
        AccountEntity accountEntity = new AccountEntity();
        DepositEntity depositEntity = new DepositEntity();
        depositEntity.setDepositIdentifier(account.getDepositIdentifier().value());

        expect(accountRepositoryMock.findByAccountId(account.getAccountId().accountId())).andReturn(accountEntity);
        expect(depositRepositoryMock.findByAccountAccountIdOrderByDateDesc(
                account.getAccountId().accountId())).andReturn(Collections.singletonList(depositEntity));
        expect(accountRepositoryMock.saveAndFlush(capture(eventCapture))).andReturn(accountEntity);

        // Act
        replay(accountRepositoryMock, depositRepositoryMock);
        dbAccountProvider.update(account);
        AccountEntity captureAccount = eventCapture.getValue();

        // Assert
        assertTrue(captureAccount.getDeposits().stream().anyMatch(d ->
                d.getMoney().equals(account.getDepositMoney().value())));

        verify(accountRepositoryMock, depositRepositoryMock);

    }

    @DisplayName("update with deposit status requested")
    @Test
    public void testUpdate_givenAccountWithDepositStatusRequested_thenUpdate() {

        // Arrange
        String userId = "1L";
        Text lastName  = new Text("ELIE MICHEL");
        Text firstName = new Text("NONO");
        Text email = new Text("eliewear1@gmail.com");
        UserEntity userEntity = new UserEntity();
        userEntity.setPrivilege(MANAGER);
        userEntity.setEmail("eliewear1@gmail.com");

        Deposit deposit = depositBuilder()
                .withIdentifier(DepositIdentifier.generate())
                .withName(DepositName.of(new Text("deposit name")))
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withExpirationDate(ExpirationDate.of(new Date(5, Month.JANUARY, 2024)))
                .withDepositReminderDate(new DepositReminderDate(new Date(8, Month.JANUARY, 2023)))
                .withDescription(new Description(new Text("deposit name")))
                .withMoney(DepositMoney.of(Money.of(100)))
                .withStatus(REQUESTED)
                .build();

        AccountId accountId = Account.nextAccountId();
        AccountHolder accountHolder = AccountHolder.of(new AccountHolderIdentifier(userId), lastName, firstName, email);
        Account account = Account.builder()
                .accountId(accountId)
                .accountHolder(accountHolder)
                .deposit(deposit)
                .build();

        Capture<ActivateDepositRequested> eventCapture = newCapture();
        AccountEntity accountEntity = new AccountEntity();
        DepositEntity depositEntity = new DepositEntity();
        depositEntity.setDepositIdentifier(account.getDepositIdentifier().value());

        expect(accountRepositoryMock.findByAccountId(account.getAccountId().accountId())).andReturn(accountEntity);
        expect(depositRepositoryMock.findByAccountAccountIdOrderByDateDesc(
                account.getAccountId().accountId())).andReturn(Collections.singletonList(depositEntity));
        expect(accountRepositoryMock.saveAndFlush(isA(AccountEntity.class))).andReturn(accountEntity);
        eventPublisherMock.publishEvent(capture(eventCapture));
        EasyMock.expectLastCall().andVoid().anyTimes();

        expect(userRepositoryMock.findByPrivilege(MANAGER)).andReturn(userEntity);

        // Act
        replay(accountRepositoryMock, depositRepositoryMock, eventPublisherMock, userRepositoryMock);
        dbAccountProvider.update(account);
        ActivateDepositRequested captureAccount = eventCapture.getValue();

        // Assert
        assertEquals("eliewear1@gmail.com", captureAccount.getManagerEmail());

        verify(accountRepositoryMock, depositRepositoryMock, eventPublisherMock, userRepositoryMock);
    }

    @DisplayName("update with deposit status reactivated")
    @Test
    public void testUpdate_givenAccountWithDepositStatusReactivated_thenUpdate() {

        // Arrange
        String userId = "1L";
        Text lastName  = new Text("ELIE MICHEL");
        Text firstName = new Text("NONO");
        Text email = new Text("eliewear1@gmail.com");
        UserEntity userEntity = new UserEntity();
        userEntity.setPrivilege(MANAGER);
        userEntity.setEmail("eliewear1@gmail.com");

        Deposit deposit = depositBuilder()
                .withIdentifier(DepositIdentifier.generate())
                .withName(DepositName.of(new Text("deposit name")))
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withExpirationDate(ExpirationDate.of(new Date(5, Month.JANUARY, 2024)))
                .withDepositReminderDate(new DepositReminderDate(new Date(8, Month.JANUARY, 2023)))
                .withDescription(new Description(new Text("deposit name")))
                .withMoney(DepositMoney.of(Money.of(100)))
                .withStatus(REACTIVATED)
                .build();

        AccountId accountId = Account.nextAccountId();
        AccountHolder accountHolder = AccountHolder.of(new AccountHolderIdentifier(userId), lastName, firstName, email);
        Account account = Account.builder()
                .accountId(accountId)
                .accountHolder(accountHolder)
                .deposit(deposit)
                .build();

        Capture<DepositReactivated> eventCapture = newCapture();
        AccountEntity accountEntity = new AccountEntity();
        DepositEntity depositEntity = new DepositEntity();
        depositEntity.setDepositIdentifier(account.getDepositIdentifier().value());

        expect(accountRepositoryMock.findByAccountId(account.getAccountId().accountId())).andReturn(accountEntity);
        expect(depositRepositoryMock.findByAccountAccountIdOrderByDateDesc(
                account.getAccountId().accountId())).andReturn(Collections.singletonList(depositEntity));
        expect(accountRepositoryMock.saveAndFlush(isA(AccountEntity.class))).andReturn(accountEntity);
        eventPublisherMock.publishEvent(capture(eventCapture));
        EasyMock.expectLastCall().andVoid().anyTimes();


        // Act
        replay(accountRepositoryMock, depositRepositoryMock, eventPublisherMock);
        dbAccountProvider.update(account);
        DepositReactivated captureAccount = eventCapture.getValue();

        // Assert
        assertEquals("eliewear1@gmail.com", captureAccount.getAccountHolderEmailAddress().value());
    }
}
