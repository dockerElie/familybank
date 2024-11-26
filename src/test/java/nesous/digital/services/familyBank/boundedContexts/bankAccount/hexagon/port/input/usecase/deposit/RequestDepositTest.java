package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.DepositCommand;
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

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.*;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestDepositTest {

    DepositProvider depositProvider = createMock(DepositProvider.class);

    AccountProvider accountProvider = createMock(AccountProvider.class);

    private Date date;

    private RequestDeposit requestDeposit;

    @BeforeEach
    public void setUp() {

        date = new Date(LocalDate.now().getDayOfMonth(), LocalDate.now().getMonth(),
                LocalDate.now().getYear());
        requestDeposit = new RequestDeposit(depositProvider, accountProvider);
    }

    @DisplayName("request deposit with invalid status")
    @Test
    public void testExecute_givenIdentifierAndInvalidStatus_thenExecute() {

        // Arrange
        AccountId accountId = Account.nextAccountId();
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date noExpiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
        DepositCommand depositCommand = DepositCommand.builder()
                .depositIdentifier(depositIdentifier)
                .depositName(DepositName.of(new Text("deposit_name")))
                .description(new Description(new Text("description")))
                .date(date)
                .expirationDate(ExpirationDate.of(noExpiredDate))
                .status(DENIED)
                .reason(new Reason(new Text("reason")))
                .build();

        Deposit deposit = depositBuilder()
                .withIdentifier(depositCommand.getDepositIdentifier())
                .withName(depositCommand.getDepositName())
                .withDescription(depositCommand.getDescription())
                .withDate(depositCommand.getDate())
                .withExpirationDate(depositCommand.getExpirationDate())
                .withStatus(depositCommand.getStatus())
                .withReason(depositCommand.getReason())
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

        // Act
        replay(depositProvider, accountProvider);
        ValidationException exception =  assertThrows(ValidationException.class, () ->
                requestDeposit.execute(depositCommand));

        // Assert
        Assertions.assertEquals("Deposit can be requested only if the status is Validated, Done, Cancelled or Closed", exception.getMessage());
    }

    @DisplayName("request deposit with valid status")
    @Test
    public void testExecute_givenIdentifierAndStatusIsDone_thenExecute() {

        // Arrange
        Capture<Account> accountCapture = newCapture();
        AccountId accountId = Account.nextAccountId();
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date noExpiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
        DepositCommand depositCommand = DepositCommand.builder()
                .depositIdentifier(depositIdentifier)
                .depositName(DepositName.of(new Text("deposit_name")))
                .description(new Description(new Text("description")))
                .date(date)
                .expirationDate(ExpirationDate.of(noExpiredDate))
                .status(DONE)
                .reason(new Reason(new Text("reason")))
                .build();

        Deposit deposit = depositBuilder()
                .withIdentifier(depositIdentifier)
                .withName(DepositName.of(new Text("deposit_name")))
                .withDescription(new Description(new Text("description")))
                .withDate(date)
                .withExpirationDate(ExpirationDate.of(noExpiredDate))
                .withStatus(DONE)
                .withReason(new Reason(new Text("reason")))
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
        Deposit result = requestDeposit.execute(depositCommand);
        Account accountUpdated = accountCapture.getValue();

        // Assert
        assertEquals(REQUESTED, accountUpdated.getDepositStatus());
        assertEquals(REQUESTED, result.getStatus());
    }

    @DisplayName("request deposit with status closed")
    @Test
    public void testExecute_givenIdentifierAndStatusIsClosed_thenExecute() {

        // Arrange
        Capture<Account> accountCapture = newCapture();
        AccountId accountId = Account.nextAccountId();
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() - 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date expiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
        DepositCommand depositCommand = DepositCommand.builder()
                .depositIdentifier(depositIdentifier)
                .depositName(DepositName.of(new Text("deposit_name")))
                .description(new Description(new Text("description")))
                .date(date)
                .expirationDate(ExpirationDate.of(expiredDate))
                .status(CLOSED)
                .reason(new Reason(new Text("reason")))
                .build();

        Deposit deposit = depositBuilder()
                .withIdentifier(depositIdentifier)
                .withName(DepositName.of(new Text("deposit_name")))
                .withDescription(new Description(new Text("description")))
                .withDate(date)
                .withExpirationDate(ExpirationDate.of(expiredDate))
                .withStatus(CLOSED)
                .withReason(new Reason(new Text("reason")))
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
        Deposit result = requestDeposit.execute(depositCommand);
        Account accountUpdated = accountCapture.getValue();

        // Assert
        assertEquals(REQUESTED, accountUpdated.getDepositStatus());
        assertEquals(REQUESTED, result.getStatus());
    }
}
