package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain;


import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolder;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolderIdentifier;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationException;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationMessages;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    private static Stream<Arguments> testCreateDepositAccount_whenGivenAccountIdAndAccountHolder_thenReturnNewAccount() {
        String userId = "1L";
        AccountId accountId = Account.nextAccountId();
        Text lastName  = new Text("NONO");
        Text firstName = new Text("ELIE MICHEL");
        Text email = new Text("eliewear1@gmail.com");
        AccountHolder accountHolder = AccountHolder.of(new AccountHolderIdentifier(userId), lastName, firstName, email);
        return Stream.of(Arguments.of(accountId, accountHolder));
    }

    private static Stream<Arguments> testCreateDepositAccount_whenGivenEmptyAccountHolderId_throwValidationException() {

        Text lastName  = new Text("NONO");
        Text firstName = new Text("ELIE MICHEL");
        return Stream.of(Arguments.of(lastName, firstName));
    }

    private static Stream<Arguments> testCreateDepositAccount_whenGivenEmptyLastNameAndEmptyEmail_throwValidationException() {

        Text firstName = new Text("ELIE MICHEL");
        String userId = "1L";
        return Stream.of(Arguments.of(userId, firstName));
    }

    private static Stream<Arguments> testCreateDepositAccount_whenGivenEmptyFirstNameAndEmptyEmail_throwValidationException() {

        Text lastName = new Text("NONO");
        String userId = "1L";

        return Stream.of(Arguments.of(lastName, userId));
    }

    private static Stream<Arguments> testCreateDepositAccount_whenGivenEmptyEmail_throwValidationException() {

        String userId = "1L";
        Text lastName  = new Text("NONO");
        Text firstName = new Text("ELIE MICHEL");
        return Stream.of(Arguments.of(userId, firstName, lastName));
    }

    private static Stream<Arguments> testCreateDepositAccount_whenGivenInvalidEmail_throwValidationException() {

        String userId = "1L";
        Text lastName  = new Text("ELIE MICHEL");
        Text firstName = new Text("NONO");
        Text email = new Text("eli");
        return Stream.of(Arguments.of(userId, firstName, lastName, email));
    }


    @ParameterizedTest
    @MethodSource
    @DisplayName("depositBankAccount")
    public void testCreateDepositAccount_whenGivenAccountIdAndAccountHolder_thenReturnNewAccount(AccountId accountId,
                                                                                                           AccountHolder accountHolder) {
        // Arrange & Act
        Account account = Account
                .builder().accountId(accountId).accountHolder(accountHolder).build();

        // Assert
        assertNotNull(account);
        assertEquals(accountId.accountId(), account.getAccountId().accountId());
        assertEquals(accountHolder.getFullName().value(), account.getAccountHolderName().value());
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("depositBankAccountWithoutAccountHolderId")
    public void testCreateDepositAccount_whenGivenEmptyAccountHolderId_throwValidationException(Text lastName, Text firstName) {

        // ARRANGE & ACT
        ValidationException exception =  assertThrows(ValidationException.class, () -> AccountHolder.of(new AccountHolderIdentifier(null),
                lastName, firstName, null));

        // Assert
        assertEquals(ValidationMessages.ACCOUNT_ID_EMPTY, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("depositBankAccountWithoutAccountHolderLastName")
    public void testCreateDepositAccount_whenGivenEmptyLastNameAndEmptyEmail_throwValidationException(String userId, Text firstName) {

        // Arrange & ACT
        ValidationException exception =  assertThrows(ValidationException.class, () -> AccountHolder.of(new AccountHolderIdentifier(userId),
                null, firstName, null));

        // Assert
        assertEquals(ValidationMessages.ACCOUNT_HOLDER_LAST_NAME_EMPTY, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("depositBankAccountWithoutAccountHolderFirstName")
    public void testCreateDepositAccount_whenGivenEmptyFirstNameAndEmptyEmail_throwValidationException(Text lastName, String userId) {

        // Arrange & Act
        ValidationException exception =  assertThrows(ValidationException.class, () -> AccountHolder.of(new AccountHolderIdentifier(userId), lastName,
                null, null));

        // Assert
        assertEquals(ValidationMessages.ACCOUNT_HOLDER_FIRST_NAME_EMPTY, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("depositBankAccountWithoutAccountHolderEmail")
    public void testCreateDepositAccount_whenGivenEmptyEmail_throwValidationException(String userId, Text firstName,
                                                                                      Text lastName) {

        // Arrange & Act
        ValidationException exception =  assertThrows(ValidationException.class, () -> AccountHolder.of(new AccountHolderIdentifier(userId), lastName,
                firstName, null));

        // Assert
        assertEquals(nesous.digital.services.familyBank.boundedContexts.shareKernel.exceptions.ValidationMessages.ACCOUNT_HOLDER_EMAIL_EMPTY, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("depositBankAccountWithInvalidAccountHolderEmail")
    public void testCreateDepositAccount_whenGivenInvalidEmail_throwValidationException(String userId, Text firstName,
                                                                                        Text lastName, Text emailAddress) {

        // Arrange & Act
        ValidationException exception =  assertThrows(ValidationException.class, () -> AccountHolder.of(new AccountHolderIdentifier(userId), lastName,
                firstName, emailAddress));

        // Assert
        assertEquals(nesous.digital.services.familyBank.boundedContexts.shareKernel.exceptions.ValidationMessages.INVALID_EMAIL_ADDRESS, exception.getMessage());
    }
}
