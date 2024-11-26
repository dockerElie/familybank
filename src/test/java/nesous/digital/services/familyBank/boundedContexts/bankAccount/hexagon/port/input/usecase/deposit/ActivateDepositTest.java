package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;


import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.logging.LogEntry;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.logging.LoggingProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.DepositCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.DepositAccountException;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationException;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.DateConversion;
import org.easymock.Capture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.ACTIVATED;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.VALIDATED;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class
ActivateDepositTest {

    private final AccountProvider accountProvider = createMock(AccountProvider.class);
    private final LoggingProvider loggingProvider = createMock(LoggingProvider.class);

    private AccountId accountId;

    private DepositName depositName;

    private Description description;

    private Date date;

    private ActivateDeposit activateDeposit;

    private Account account;

    private Deposit lastDeposit;

    @BeforeEach
    public void setUp() {
        accountId = Account.nextAccountId();
        depositName = DepositName.of(new Text("deposit"));
        description = new Description(null);
        date = new Date(LocalDate.now().getDayOfMonth(), LocalDate.now().getMonth(),
                LocalDate.now().getYear());
        activateDeposit = new ActivateDeposit(accountProvider, loggingProvider);

        lastDeposit = depositBuilder()
                .withIdentifier(DepositIdentifier.generate())
                .withName(DepositName.of(new Text("deposit name")))
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withExpirationDate(ExpirationDate.of(new Date(1, Month.JANUARY, 2024)))
                .withDescription(new Description(new Text("deposit name")))
                .withMoney(DepositMoney.of(Money.of(100)))
                .withStatus(VALIDATED)
                .build();

        account = Account.builder()
                .accountId(accountId)
                .accountHolder(AccountHolder.of(new AccountHolderIdentifier("1L"),
                        new Text("lastName"),
                        new Text("firstName"),
                        new Text("lastFirst@bro.com")))
                .deposit(lastDeposit)
                .build();
    }

    @Test
    @DisplayName("successfully activate a deposit")
    public void testActivateDeposit_GivenAccountIdAndValidDepositDetails_thenActivate() {

        // Arrange
        Capture<LogEntry> logEntryCapture = newCapture();

        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date expiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
        ExpirationDate expirationDate = ExpirationDate.of(expiredDate);

        DepositCommand depositCommand = DepositCommand.builder()
                .depositName(depositName)
                .description(description)
                .date(date)
                .expirationDate(expirationDate)
                .build();

        expect(accountProvider.withLastDeposit(accountId)).andReturn(account);
        expect(accountProvider.activateDeposit(isA(Account.class))).andReturn(
                Account.builder()
                        .accountId(accountId)
                        .accountHolder(AccountHolder.of(new AccountHolderIdentifier("1L"),
                                new Text("lastName"),
                                new Text("firstName"),
                                new Text("lastFirst@bro.com")))
                        .deposit(depositBuilder()
                                .withIdentifier(DepositIdentifier.generate())
                                .withName(depositName)
                                .withDate(date)
                                .withExpirationDate(expirationDate)
                                .withStatus(ACTIVATED)
                                .withDescription(description)
                                .withMoney(DepositMoney.of(Money.of(100))).build()
                        ).build());
        loggingProvider.log(capture(logEntryCapture));
        expectLastCall().andVoid().anyTimes();

        // Act
        replay(accountProvider, loggingProvider);
        Account accountActivated = activateDeposit.execute(accountId, depositCommand);

        // Assert
        assertNotNull(accountActivated.getDepositIdentifier());
        assertEquals(depositName, accountActivated.getDepositName());
        assertEquals(description, accountActivated.getDepositDescription());
        assertEquals(ACTIVATED, accountActivated.getDepositStatus());
        assertEquals(100, accountActivated.getDepositMoney().value());
    }

    @Test
    @DisplayName("Activate a deposit with no expiration date")
    public void testActivateDeposit_GivenDepositDetailsWithNoExpirationDate_thenThrowException() {

        // Arrange & Act
        ValidationException exception = assertThrows(
                ValidationException.class, ()-> DepositCommand.builder()
                        .depositName(depositName)
                        .description(description)
                        .date(date)
                        .expirationDate(ExpirationDate.of(null))
                        .build());

        // Assert
        assertEquals("Expiration date is empty", exception.getMessage());

    }

    @Test
    @DisplayName("Activate a deposit with expiration date before the deposit creation date ")
    public void testActivateDeposit_GivenDepositDetailsWithExpirationDateSetInThePast_thenThrowException() {

        // Arrange
        expect(accountProvider.withLastDeposit(accountId)).andReturn(account);
        DepositCommand depositCommand = DepositCommand.builder()
                .depositName(depositName)
                .description(description)
                .date(date)
                .expirationDate(
                        ExpirationDate.of(new Date(31, Month.DECEMBER, Year.of(2023).getValue())))
                .build();

        // Act
        replay(accountProvider);
        ValidationException exception =
                assertThrows(ValidationException.class,
                        () -> activateDeposit.execute(accountId, depositCommand));

        // Assert
        assertEquals("Expiration date is before the deposit date", exception.getMessage());
    }

    @Test
    @DisplayName("activate deposit for invalid account identifier")
    public void testActivateDeposit_whenDepositBankAccountIdentifierDoesNotExist_thenThrowException() {

        // Arrange
        ExpirationDate expirationDate = ExpirationDate.of(new Date(LocalDate.now().getDayOfMonth() + 3,
                LocalDate.now().getMonth(), LocalDate.now().getYear()));
        DepositCommand depositCommand = DepositCommand.builder()
                .depositName(depositName)
                .description(description)
                .date(date)
                .expirationDate(expirationDate)
                .build();

        // Act
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> activateDeposit.execute(null, depositCommand));

        // Assert
        assertEquals("Invalid Account", exception.getMessage());
    }

    @Test
    @DisplayName("activate deposit for invalid account")
    public void testActivateDeposit_whenDepositBankAccountDoesNotExist_thenThrowException() {

        // Arrange
        ExpirationDate expirationDate = ExpirationDate.of(new Date(LocalDate.now().getDayOfMonth() + 3,
                LocalDate.now().getMonth(), LocalDate.now().getYear()));
        DepositCommand depositCommand = DepositCommand.builder()
                .depositName(depositName)
                .description(description)
                .date(date)
                .expirationDate(expirationDate)
                .build();

        expect(accountProvider.withLastDeposit(accountId)).andReturn(null);

        // Act
        replay(accountProvider);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> activateDeposit.execute(accountId, depositCommand));

        // Assert
        assertEquals("Invalid Account", exception.getMessage());
    }

    @Test
    @DisplayName("Activate a deposit with empty name text")
    public void testActivateDeposit_GivenDepositDetailsWithEmptyNameText_thenThrowException() {

        // Arrange & Act
        ValidationException exception = assertThrows(
                ValidationException.class, ()-> DepositCommand.builder()
                        .depositName(DepositName.of(new Text("")))
                        .description(description)
                        .date(date)
                        .expirationDate(ExpirationDate.of(null))
                        .build());

        // Assert
        assertEquals("deposit name is empty", exception.getMessage());
    }

    @Test
    @DisplayName("Activate a deposit with empty name")
    public void testActivateDeposit_GivenDepositDetailsWithEmptyName_thenThrowException() {

        // Arrange & Act
        ValidationException exception = assertThrows(
                ValidationException.class, ()-> DepositCommand.builder()
                        .depositName(DepositName.of(null))
                        .description(description)
                        .date(date)
                        .expirationDate(ExpirationDate.of(null))
                        .build());

        // Assert
        assertEquals("deposit name is empty", exception.getMessage());
    }

    @Test
    @DisplayName("Activate a deposit, previous deposit is still valid")
    public void testActivateDeposit_GivenDepositDetailsAndPreviousDepositStillValid_thenThrowException() {

        // Arrange
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date noExpiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
        ExpirationDate expirationDate = ExpirationDate.of(noExpiredDate);

        lastDeposit = depositBuilder()
                .withIdentifier(DepositIdentifier.generate())
                .withName(DepositName.of(new Text("deposit name")))
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withExpirationDate(expirationDate)
                .withDescription(new Description(new Text("deposit name")))
                .withMoney(DepositMoney.of(Money.of(100)))
                .withStatus(VALIDATED)
                .build();

        account = Account.builder()
                .accountId(accountId)
                .accountHolder(AccountHolder.of(new AccountHolderIdentifier("1L"),
                        new Text("lastName"),
                        new Text("firstName"),
                        new Text("lastFirst@bro.com")))
                .deposit(lastDeposit)
                .build();

        DepositCommand depositCommand = DepositCommand.builder()
                .depositName(depositName)
                .description(description)
                .date(date)
                .expirationDate(expirationDate)
                .build();

        expect(accountProvider.withLastDeposit(accountId)).andReturn(account);

        // Arrange
        replay(accountProvider);
        DepositAccountException exception = assertThrows(
                DepositAccountException.class, ()-> activateDeposit.execute(accountId, depositCommand));

        // Assert
        assertEquals("Previous deposit still ongoing", exception.getMessage());
    }
}
