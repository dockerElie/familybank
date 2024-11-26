package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationException;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.DateConversion;
import org.easymock.Capture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.*;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CancelDepositTest {

    private CancelDeposit cancelDeposit;

    private Date date;

    DepositProvider depositProvider = createMock(DepositProvider.class);
    AccountProvider accountProvider = createMock(AccountProvider.class);
    @BeforeEach
    public void setUp() {
        date = new Date(LocalDate.now().getDayOfMonth(), LocalDate.now().getMonth(),
                LocalDate.now().getYear());
        cancelDeposit = new CancelDeposit(depositProvider, accountProvider);
    }

    @DisplayName("cancel deposit")
    @Test
    public void testExecute_givenIdentifierAndValidStatus_thenExecute() {

        // Arrange
        Capture<Account> accountCapture = newCapture();
        AccountId accountId = Account.nextAccountId();
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date noExpiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());

        Deposit deposit = depositBuilder()
                .withIdentifier(depositIdentifier)
                .withName(DepositName.of(new Text("deposit_name")))
                .withDate(date)
                .withMoney(DepositMoney.of(Money.of(100.0)))
                .withExpirationDate(ExpirationDate.of(noExpiredDate))
                .withAccountId(accountId)
                .withStatus(DONE)
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
        Deposit response = cancelDeposit.execute(depositIdentifier);
        Account accountUpdated = accountCapture.getValue();

        // Assert
        assertEquals(CANCELLED, accountUpdated.getDepositStatus());
        assertEquals(Money.ZERO, response.getMoney().money());
        assertEquals(CANCELLED, response.getStatus());

        verify(depositProvider, accountProvider);
    }


    @DisplayName("can't cancel deposit")
    @Test
    public void testExecute_givenIdentifierAndInValidStatus_thenExecute() {

        // Arrange
        AccountId accountId = Account.nextAccountId();
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date noExpiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());

        Deposit deposit = depositBuilder()
                .withIdentifier(depositIdentifier)
                .withName(DepositName.of(new Text("deposit_name")))
                .withDate(date)
                .withMoney(DepositMoney.of(Money.of(100.0)))
                .withExpirationDate(ExpirationDate.of(noExpiredDate))
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
                cancelDeposit.execute(depositIdentifier));

        // Assert
        assertEquals("Deposit can be cancelled only if the status is Validated or Done", exception.getMessage());

        verify(depositProvider, accountProvider);
    }
}
