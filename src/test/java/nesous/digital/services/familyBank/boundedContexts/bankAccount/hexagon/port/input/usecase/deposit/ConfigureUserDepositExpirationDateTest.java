package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.DepositCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.DepositException;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationException;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.DateConversion;
import org.easymock.Capture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.*;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigureUserDepositExpirationDateTest {

    DepositProvider depositProvider = createMock(DepositProvider.class);
    AccountProvider accountProvider = createMock(AccountProvider.class);
    private ConfigureUserDepositExpirationDate configureUserDepositExpirationDate;

    private Date date;

    @BeforeEach
    public void setUp() {

        date = new Date(LocalDate.now().getDayOfMonth(), LocalDate.now().getMonth(),
                LocalDate.now().getYear());
        configureUserDepositExpirationDate = new ConfigureUserDepositExpirationDate(depositProvider, accountProvider);
    }

    @Test
    public void testExecute_givenDepositIdentifierAndUserExpirationDate_thenSetUserExpirationDate() {

        // Arrange
        Capture<Account> accountCapture = newCapture();
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date noExpiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
        Date userExpirationDate = noExpiredDate.plusDays(100);
        AccountId accountId = Account.nextAccountId();

        DepositCommand depositCommand = DepositCommand.builder()
                .depositIdentifier(depositIdentifier)
                .depositName(DepositName.of(new Text("deposit_name")))
                .description(new Description(new Text("description")))
                .date(date)
                .expirationDate(ExpirationDate.of(noExpiredDate))
                .userExpirationDate(new UserExpirationDate(userExpirationDate))
                .build();

        Deposit deposit = depositBuilder()
                .withIdentifier(depositCommand.getDepositIdentifier())
                .withName(depositCommand.getDepositName())
                .withDescription(depositCommand.getDescription())
                .withDate(depositCommand.getDate())
                .withExpirationDate(depositCommand.getExpirationDate())
                .withStatus(DONE)
                .withAccountId(accountId)
                .build();
        AccountHolderIdentifier accountHolderIdentifier = new AccountHolderIdentifier("xds-qsds-zesda");
        Text lastName = new Text("lastName");
        Text firstName = new Text("firstName");
        Text email = new Text("emlie@com");
        AccountHolder accountHolder = AccountHolder.of(accountHolderIdentifier, lastName, firstName, email);
        Account account = Account.builder()
                .accountHolder(accountHolder)
                .deposit(deposit)
                .build();

        expect(depositProvider.of(depositIdentifier)).andReturn(deposit);
        expect(accountProvider.of(isA(AccountId.class))).andReturn(account);
        accountProvider.update(capture(accountCapture));
        expectLastCall();

        // Act
        replay(depositProvider, accountProvider);
        Deposit result = configureUserDepositExpirationDate.execute(depositCommand);

        // Assert
        assertNotNull(result.getUserExpirationDate().date());
    }

    @Test
    public void testExecute_givenDepositIdentifierWithInvalidStatus_thenThrowValidationException() {

        // Arrange
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        AccountId accountId = Account.nextAccountId();
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date noExpiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
        Date userExpirationDate = noExpiredDate.plusDays(100);

        DepositCommand depositCommand = DepositCommand.builder()
                .depositIdentifier(depositIdentifier)
                .depositName(DepositName.of(new Text("deposit_name")))
                .description(new Description(new Text("description")))
                .date(date)
                .expirationDate(ExpirationDate.of(noExpiredDate))
                .userExpirationDate(new UserExpirationDate(userExpirationDate))
                .build();

        Deposit deposit = depositBuilder()
                .withIdentifier(depositCommand.getDepositIdentifier())
                .withName(depositCommand.getDepositName())
                .withDescription(depositCommand.getDescription())
                .withDate(depositCommand.getDate())
                .withExpirationDate(depositCommand.getExpirationDate())
                .withUserExpirationDate(depositCommand.getUserExpirationDate())
                .withStatus(ACTIVATED)
                .withAccountId(accountId)
                .build();

        AccountHolderIdentifier accountHolderIdentifier = new AccountHolderIdentifier("xds-qsds-zesda");
        Text lastName = new Text("lastName");
        Text firstName = new Text("firstName");
        Text email = new Text("emlie@com");
        AccountHolder accountHolder = AccountHolder.of(accountHolderIdentifier, lastName, firstName, email);
        Account account = Account.builder()
                .accountHolder(accountHolder)
                .deposit(deposit)
                .build();

        expect(accountProvider.of(accountId)).andReturn(account);
        expect(depositProvider.of(depositIdentifier)).andReturn(deposit);

        // Act
        replay(depositProvider, accountProvider);
        ValidationException exception =  assertThrows(ValidationException.class, () ->
                configureUserDepositExpirationDate.execute(depositCommand));

        // Assert
        Assertions.assertEquals("Please make a deposit before configuring user expiration date", exception.getMessage());
    }

    @Test
    public void testExecute_givenExpiredDepositIdentifier_thenThrowDepositException() {

        // Arrange
        Capture<Account> accountCapture = newCapture();
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        AccountId accountId = Account.nextAccountId();
        Date expiredDate = new Date(25, Month.JANUARY, 2024);
        Date userExpiredDate = expiredDate.plusDays(100);

        DepositCommand depositCommand = DepositCommand.builder()
                .depositIdentifier(depositIdentifier)
                .depositName(DepositName.of(new Text("deposit_name")))
                .description(new Description(new Text("description")))
                .date(new Date(3, Month.JANUARY, 2023))
                .expirationDate(ExpirationDate.of(expiredDate))
                .userExpirationDate(new UserExpirationDate(userExpiredDate))
                .build();

        Deposit deposit = depositBuilder()
                .withIdentifier(depositCommand.getDepositIdentifier())
                .withName(depositCommand.getDepositName())
                .withDate(depositCommand.getDate())
                .withExpirationDate(depositCommand.getExpirationDate())
                .withUserExpirationDate(depositCommand.getUserExpirationDate())
                .withStatus(DONE)
                .withAccountId(accountId)
                .build();
        AccountHolderIdentifier accountHolderIdentifier = new AccountHolderIdentifier("xds-qsds-zesda");
        Text lastName = new Text("lastName");
        Text firstName = new Text("firstName");
        Text email = new Text("emlie@com");
        AccountHolder accountHolder = AccountHolder.of(accountHolderIdentifier, lastName, firstName, email);
        Account account = Account.builder()
                .accountHolder(accountHolder)
                .deposit(deposit)
                .build();

        expect(depositProvider.of(depositIdentifier)).andReturn(deposit);
        expect(accountProvider.of(isA(AccountId.class))).andReturn(account);
        accountProvider.update(capture(accountCapture));
        expectLastCall();

        // Act
        replay(depositProvider, accountProvider);
        DepositException exception =  assertThrows(DepositException.class, () ->
                configureUserDepositExpirationDate.execute(depositCommand));
        Account accountUpdated = accountCapture.getValue();

        // Assert
        assertEquals(CLOSED, accountUpdated.getDepositStatus());
        assertEquals("Deposit already expired.", exception.getMessage());
    }
}
