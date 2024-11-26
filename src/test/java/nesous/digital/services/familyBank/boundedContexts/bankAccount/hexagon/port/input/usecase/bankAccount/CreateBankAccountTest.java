package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.bankAccount;


import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolder;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolderIdentifier;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.logging.LogEntry;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.logging.LoggingProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.BankAccountCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import org.easymock.Capture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateBankAccountTest {

    private CreateBankAccount createBankAccount;

    private final AccountProvider accountProvider = createMock(AccountProvider.class);
    private final LoggingProvider loggingProvider = createMock(LoggingProvider.class);

    private static Stream<Arguments> testExecute_GivenACreateBankAccountCommand_thenExecute() {
        String userId = "1L";
        Text lastName  = new Text("NONO");
        Text firstName = new Text("ELIE MICHEL");
        Text email = new Text("eliewear1@gmail.com");
        AccountId accountId = Account.nextAccountId();
        AccountHolder accountHolder = AccountHolder.of(new AccountHolderIdentifier(userId), lastName, firstName, email);
        BankAccountCommand command = BankAccountCommand.builder()
                .accountId(accountId)
                .accountHolder(accountHolder)
                .build();
        return Stream.of(Arguments.of(command));
    }

    @BeforeEach
    public void setup() {
        createBankAccount = new CreateBankAccount(accountProvider, loggingProvider);
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("createDepositBankAccount")
    public void testExecute_GivenACreateBankAccountCommand_thenExecute(BankAccountCommand command) {

        // Arrange
        Capture<LogEntry> logEntryCapture = newCapture();
        Account depositAccount = Account
                .builder().accountId(command.getAccountId()).accountHolder(command.getAccountHolder()).build();
        expect(accountProvider.save(command)).andReturn(depositAccount);

        loggingProvider.log(capture(logEntryCapture));
        expectLastCall().andVoid().anyTimes();

        // Act
        replay(accountProvider, loggingProvider);
        Account account = createBankAccount.execute(command);

        // Assert
        LogEntry captureLog = logEntryCapture.getValue();
        assertEquals(LogEntry.Level.INFO, captureLog.getLevel());
        assertEquals("Creating Deposit bank account for NONO ELIE MICHEL", captureLog.getMessage());
        assertEquals(command.getAccountHolder().getFullName(), account.getAccountHolderName());
        verify(accountProvider);
    }
}
