package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.model.DepositRequestUser;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.DepositCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.DateConversion;
import org.easymock.Capture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.*;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class ValidateOrRejectDepositRequestTest {

    private final DepositProvider depositProvider = createMock(DepositProvider.class);

    private final AccountProvider accountProvider = createMock(AccountProvider.class);

    private Date date;

    @BeforeEach
    public void setUp() {
        date = new Date(LocalDate.now().getDayOfMonth(), LocalDate.now().getMonth(),
                LocalDate.now().getYear());
    }

    @DisplayName("accept request to activate deposit")
    @Test
    public void testAccept_givenDepositIdentifier_thenAccept() {

        // Arrange
        Capture<Account> accountCapture = newCapture();
        AccountId accountId = Account.nextAccountId();
        String depositIdentifier = "zer-erer-zzer";
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date noExpiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());

        LocalDate localDate1 = localDate.plusDays(10);
        Date newExpiredDate = new Date(localDate1.getDayOfMonth(), localDate1.getMonth(), localDate1.getYear());
        Deposit existDeposit = depositBuilder()
                .withIdentifier(new DepositIdentifier(depositIdentifier))
                .withName(new DepositName(new Text("deposit_name")))
                .withDescription(new Description(new Text("description_name")))
                .withMoney(DepositMoney.of(Money.of(100)))
                .withDate(date)
                .withStatus(REQUESTED)
                .withExpirationDate(ExpirationDate.of(noExpiredDate))
                .withUserExpirationDate(null)
                .withReason(new Reason(new Text("")))
                .withAccountId(accountId)
                .build();

        DepositCommand depositCommand = DepositCommand.builder()
                .depositIdentifier(existDeposit.getIdentifier())
                .depositName(existDeposit.getName())
                .description(existDeposit.getDescription())
                .date(existDeposit.getDate())
                .status(existDeposit.getStatus())
                .expirationDate(ExpirationDate.of(newExpiredDate))
                .userExpirationDate(existDeposit.getUserExpirationDate())
                .depositMoney(existDeposit.getMoney())
                .reason(new Reason(new Text("")))
                .build();

        AccountHolderIdentifier accountHolderIdentifier = new AccountHolderIdentifier("xds-qsds-zesda");
        Text lastName = new Text("lastName");
        Text firstName = new Text("firstName");
        Text email = new Text("emlie@com");
        AccountHolder accountHolder = AccountHolder.of(accountHolderIdentifier, lastName, firstName, email);
        Account account = Account.builder()
                .accountHolder(accountHolder)
                .deposit(existDeposit)
                .build();

        expect(depositProvider.of(new DepositIdentifier(depositIdentifier))).andReturn(existDeposit);
        expect(accountProvider.of(isA(AccountId.class))).andReturn(account);
        accountProvider.update(capture(accountCapture));
        expectLastCall();

        // Act
        replay(depositProvider, accountProvider);
        ValidateOrRejectDepositRequest validateOrRejectDepositRequest = new ValidateOrRejectDepositRequest(
                depositProvider, accountProvider);
        Deposit result = validateOrRejectDepositRequest.accept(depositCommand);
        Account accountUpdated = accountCapture.getValue();

        // Assert
        assertEquals(Money.ZERO, result.getMoney().money());
        assertNull(result.getUserExpirationDate());
        assertEquals(REACTIVATED, accountUpdated.getDepositStatus());
        assertEquals(REACTIVATED, result.getStatus());
        assertNotEquals(existDeposit.getExpirationDate(), result.getExpirationDate());
    }

    @DisplayName("reject request to activate deposit")
    @Test
    public void testReject_givenDepositIdentifier_thenReject() {

        // Arrange
        Capture<Account> accountCapture = newCapture();
        AccountId accountId = Account.nextAccountId();
        String depositIdentifier = "zer-erer-zzer";
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date noExpiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());

        Deposit existDeposit = depositBuilder()
                .withIdentifier(new DepositIdentifier(depositIdentifier))
                .withName(new DepositName(new Text("deposit_name")))
                .withDescription(new Description(new Text("description_name")))
                .withMoney(DepositMoney.of(Money.of(100)))
                .withDate(date)
                .withStatus(REQUESTED)
                .withExpirationDate(ExpirationDate.of(noExpiredDate))
                .withUserExpirationDate(null)
                .withReason(new Reason(new Text("")))
                .withAccountId(accountId)
                .build();

        DepositCommand depositCommand = DepositCommand.builder()
                .depositIdentifier(existDeposit.getIdentifier())
                .depositName(existDeposit.getName())
                .description(existDeposit.getDescription())
                .date(existDeposit.getDate())
                .status(existDeposit.getStatus())
                .expirationDate(existDeposit.getExpirationDate())
                .userExpirationDate(existDeposit.getUserExpirationDate())
                .depositMoney(existDeposit.getMoney())
                .reason(new Reason(new Text("")))
                .build();

        AccountHolderIdentifier accountHolderIdentifier = new AccountHolderIdentifier("xds-qsds-zesda");
        Text lastName = new Text("lastName");
        Text firstName = new Text("firstName");
        Text email = new Text("emlie@com");
        AccountHolder accountHolder = AccountHolder.of(accountHolderIdentifier, lastName, firstName, email);
        Account account = Account.builder()
                .accountHolder(accountHolder)
                .deposit(existDeposit)
                .build();

        expect(depositProvider.of(new DepositIdentifier(depositIdentifier))).andReturn(existDeposit);
        expect(accountProvider.of(isA(AccountId.class))).andReturn(account);
        accountProvider.update(capture(accountCapture));
        expectLastCall();

        // Act
        replay(depositProvider, accountProvider);
        ValidateOrRejectDepositRequest validateOrRejectDepositRequest = new ValidateOrRejectDepositRequest(
                depositProvider, accountProvider);
        Deposit result = validateOrRejectDepositRequest.reject(depositCommand);
        Account accountUpdated = accountCapture.getValue();

        // Assert;
        assertEquals(DENIED, accountUpdated.getDepositStatus());
        assertEquals(DENIED, result.getStatus());
        assertEquals(existDeposit.getExpirationDate(), result.getExpirationDate());
    }

    @DisplayName("list of users made deposit request")
    @Test
    public void listOfUserMadeDepositRequestTest_thenReturnListOfUsers() {

        // Arrange
        AccountId accountId = Account.nextAccountId();
        String depositIdentifier = "zer-erer-zzer";
        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        LocalDate localDate = DateConversion.dateToLocalDate(calendarDate);
        Date noExpiredDate = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
        Deposit deposit = depositBuilder()
                .withIdentifier(new DepositIdentifier(depositIdentifier))
                .withName(new DepositName(new Text("deposit_name")))
                .withDescription(new Description(new Text("description_name")))
                .withMoney(DepositMoney.of(Money.of(100)))
                .withDate(date)
                .withStatus(REQUESTED)
                .withExpirationDate(ExpirationDate.of(noExpiredDate))
                .withUserExpirationDate(null)
                .withReason(new Reason(new Text("")))
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
        expect(depositProvider.searchDepositByStatus(REQUESTED)).andReturn(Collections.singletonList(deposit));
        expect(accountProvider.of(accountId)).andReturn(account);

        // Act
        replay(depositProvider, accountProvider);
        ValidateOrRejectDepositRequest validateOrRejectDepositRequest = new ValidateOrRejectDepositRequest(
                depositProvider, accountProvider);
        List<DepositRequestUser> result = validateOrRejectDepositRequest.usersMadeDepositRequest();

        // Assert
        assertNotNull(result);
        assertEquals("lastName firstName", result.get(0).getFullName());
        verify(depositProvider, accountProvider);
    }
}
