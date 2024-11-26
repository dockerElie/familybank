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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.*;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidateDepositTest {

    DepositProvider depositProvider = createMock(DepositProvider.class);
    AccountProvider accountProvider = createMock(AccountProvider.class);
    private ValidateDeposit validateDeposit;

    @BeforeEach
    public void setUp() {

        validateDeposit = new ValidateDeposit(depositProvider, accountProvider);
    }

    @DisplayName("validate deposit with invalid status")
    @Test
    public void testExecute_givenDepositIdentifierWithInvalidStatus_thenThrowValidationException() {

        // Arrange
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        AccountId accountId = Account.nextAccountId();
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date noExpiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
        Deposit deposit = depositBuilder()
                .withIdentifier(depositIdentifier)
                .withName(DepositName.of(new Text("deposit_name")))
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withExpirationDate(ExpirationDate.of(noExpiredDate))
                .withStatus(ACTIVATED)
                .withAccountId(accountId)
                .build();

        expect(depositProvider.of(depositIdentifier)).andReturn(deposit);

        // Act
        replay(depositProvider);
        ValidationException exception =  assertThrows(ValidationException.class, () ->
                validateDeposit.execute(depositIdentifier));

        // Assert
        Assertions.assertEquals("Please make a deposit before validating", exception.getMessage());
    }

    @Test
    @DisplayName("validate deposit with valid status")
    public void testExecute_givenDepositIdentifier_thenValidate() {

        // Arrange
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
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withExpirationDate(ExpirationDate.of(noExpiredDate))
                .withUserExpirationDate(new UserExpirationDate(noExpiredDate))
                .withStatus(DONE)
                .withMoney(DepositMoney.of(money))
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
        Deposit validatedDeposit = validateDeposit.execute(depositIdentifier);
        Account accountUpdated = accountCapture.getValue();

        // Assert
        assertEquals(VALIDATED, accountUpdated.getDepositStatus());
        assertEquals(Status.VALIDATED, validatedDeposit.getStatus());
    }

    @DisplayName("validate deposit with expired date")
    @Test
    public void testExecute_givenExpiredDepositIdentifier_thenThrowDepositException() {

        // Arrange
        Capture<Account> accountCapture = newCapture();
        Money money = Money.of(Double.parseDouble("100.0"));
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        AccountId accountId = Account.nextAccountId();
        Date expiredDate = new Date(25, Month.JANUARY, 2024);

        Deposit deposit = depositBuilder()
                .withIdentifier(depositIdentifier)
                .withName(DepositName.of(new Text("deposit_name")))
                .withDate(new Date(3, Month.JANUARY, 2023))
                .withExpirationDate(ExpirationDate.of(expiredDate))
                .withStatus(DONE)
                .withAccountId(accountId)
                .withMoney(DepositMoney.of(money))
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
                validateDeposit.execute(depositIdentifier));
        Account accountUpdated = accountCapture.getValue();

        // Assert
        assertEquals(CLOSED, accountUpdated.getDepositStatus());
        assertEquals("Deposit already expired.", exception.getMessage());
    }
}
