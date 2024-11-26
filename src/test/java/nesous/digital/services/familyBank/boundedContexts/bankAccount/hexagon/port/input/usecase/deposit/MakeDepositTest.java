package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.DepositException;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationException;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.DateConversion;
import org.easymock.Capture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.*;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class MakeDepositTest {

    DepositProvider depositProvider = createMock(DepositProvider.class);
    AccountProvider accountProvider = createMock(AccountProvider.class);

    @DisplayName("make deposit with empty amount")
    @Test
    public void testExecute_givenDepositIdentifierAndEmptyAmount_thenAddMoney() {

        // Arrange
        Capture<Account> accountCapture = newCapture();
        MakeDeposit makeDeposit = new MakeDeposit("", depositProvider, accountProvider);
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        AccountId accountId = Account.nextAccountId();
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date noExpiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
        Deposit deposit = depositBuilder()
                .withIdentifier(depositIdentifier)
                .withName(DepositName.of(new Text("deposit_name")))
                .withDescription(new Description(new Text("description")))
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withDepositReminderDate(new DepositReminderDate(
                        new Date(3, Month.JANUARY, 2023).plusDays(5)))
                .withExpirationDate(ExpirationDate.of(noExpiredDate))
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

        expect(depositProvider.of(depositIdentifier)).andReturn(deposit);
        expect(accountProvider.of(isA(AccountId.class))).andReturn(account);
        accountProvider.update(capture(accountCapture));
        expectLastCall();

        // Act
        replay(accountProvider, depositProvider);
        Deposit result = makeDeposit.execute(depositIdentifier);
        Account accountUpdated = accountCapture.getValue();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getDepositReminderDate());
        assertEquals(DONE, accountUpdated.getDepositStatus());
        assertEquals(DONE, result.getStatus());
        assertEquals(Money.ZERO, result.getMoney().money());
    }

    @DisplayName("make deposit")
    @Test
    public void testExecute_givenDepositIdentifier_thenAddMoney() {

        // Arrange
        MakeDeposit makeDeposit = new MakeDeposit("100.0", depositProvider, accountProvider);
        Capture<Account> accountCapture = newCapture();
        Money money = Money.of(Double.parseDouble("100.0"));
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        AccountId accountId = Account.nextAccountId();
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date noExpiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
        Deposit deposit = depositBuilder()
                .withIdentifier(depositIdentifier)
                .withName(DepositName.of(new Text("deposit_name")))
                .withDescription(new Description(new Text("description")))
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withDepositReminderDate(new DepositReminderDate(
                        new Date(3, Month.JANUARY, 2023).plusDays(5)))
                .withExpirationDate(ExpirationDate.of(noExpiredDate))
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

        expect(depositProvider.of(depositIdentifier)).andReturn(deposit);
        expect(accountProvider.of(isA(AccountId.class))).andReturn(account);
        accountProvider.update(capture(accountCapture));
        expectLastCall();

        // Arrange
        replay(accountProvider, depositProvider);
        Deposit result = makeDeposit.execute(depositIdentifier);
        Account accountUpdated = accountCapture.getValue();

        // Assert
        assertNotNull(result);
        assertNull(result.getDepositReminderDate());
        assertEquals(DONE, accountUpdated.getDepositStatus());
        assertEquals(DONE, result.getStatus());
        assertEquals(money, result.getMoney().money());
    }

    @DisplayName("make deposit with an invalid money - string value")
    @Test
    public void testExecute_givenDepositIdentifierAndNonValidNumber_thenThrowValidException() {

        // Arrange
        String money = "AZe";
        MakeDeposit makeDeposit = new MakeDeposit(money, depositProvider, accountProvider);
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        AccountId accountId = Account.nextAccountId();
        Date expiredDate = new Date(25, Month.JANUARY, 2024);
        Deposit deposit = depositBuilder()
                .withIdentifier(depositIdentifier)
                .withName(DepositName.of(new Text("deposit_name")))
                .withDescription(new Description(new Text("description")))
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withDepositReminderDate(new DepositReminderDate(
                        new Date(3, Month.JANUARY, 2023).plusDays(5)))
                .withExpirationDate(ExpirationDate.of(expiredDate))
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

        // Arrange
        replay(depositProvider);
        ValidationException exception =  assertThrows(ValidationException.class, () ->
                makeDeposit.execute(depositIdentifier));

        // Assert
        assertEquals("Wrong value. Please provide a numeric value.", exception.getMessage());
    }

    @DisplayName("make deposit with an invalid money - negative value")
    @Test
    public void testExecute_givenDepositIdentifierAndNegativeValidNumber_thenThrowValidException() {

        // Arrange
        String money = "-100.0";
        MakeDeposit makeDeposit = new MakeDeposit(money, depositProvider, accountProvider);
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        AccountId accountId = Account.nextAccountId();
        Date expiredDate = new Date(25, Month.JANUARY, 2024);
        Deposit deposit = depositBuilder()
                .withIdentifier(depositIdentifier)
                .withName(DepositName.of(new Text("deposit_name")))
                .withDescription(new Description(new Text("description")))
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withDepositReminderDate(new DepositReminderDate(
                        new Date(3, Month.JANUARY, 2023).plusDays(5)))
                .withExpirationDate(ExpirationDate.of(expiredDate))
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


        // Arrange
        replay(depositProvider, accountProvider);
        ValidationException exception =  assertThrows(ValidationException.class, () ->
                makeDeposit.execute(depositIdentifier));

        // Assert
        assertEquals("Negative value. Please provide a positive value", exception.getMessage());
    }

    @DisplayName("make deposit with expired date")
    @Test
    public void testExecute_givenExpiredDepositIdentifier_thenThrowValidException() {

        // Arrange
        Capture<Account> accountCapture = newCapture();
        MakeDeposit makeDeposit = new MakeDeposit("100.0", depositProvider, accountProvider);
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        AccountId accountId = Account.nextAccountId();
        Date expiredDate = new Date(25, Month.JANUARY, 2024);
        Deposit deposit = depositBuilder()
                .withIdentifier(depositIdentifier)
                .withName(DepositName.of(new Text("deposit_name")))
                .withDescription(new Description(new Text("description")))
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withDepositReminderDate(new DepositReminderDate(
                        new Date(3, Month.JANUARY, 2023).plusDays(5)))
                .withExpirationDate(ExpirationDate.of(expiredDate))
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

        expect(depositProvider.of(depositIdentifier)).andReturn(deposit);
        expect(accountProvider.of(isA(AccountId.class))).andReturn(account);
        accountProvider.update(capture(accountCapture));
        expectLastCall();

        // Act
        replay(depositProvider, accountProvider);
        DepositException exception =  assertThrows(DepositException.class, () ->
                makeDeposit.execute(depositIdentifier));
        Account accountUpdated = accountCapture.getValue();

        // Assert
        assertEquals(CLOSED, accountUpdated.getDepositStatus());
        assertEquals("Deposit already expired.", exception.getMessage());
    }
}
