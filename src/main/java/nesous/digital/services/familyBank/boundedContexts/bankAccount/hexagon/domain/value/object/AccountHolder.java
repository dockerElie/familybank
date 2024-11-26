package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationMessages;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Email;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;

import static nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.Guard.guard;


public record AccountHolder(AccountHolderIdentifier identifier, Text lastName, Text firstName, Email email) {

    private AccountHolder(Text lastName, Text firstName, Email email) {
        this(null, lastName, firstName, email);
    }

    private AccountHolder(Text lastName, Text firstName) {
        this(null, lastName, firstName, null);
    }

    public Text getFullName() {
        return lastName.addSpace().add(firstName);
    }

    public String getName() {

        Text fullName = lastName.addSpace().add(firstName);
        return fullName.value();
    }

    public static AccountHolder of(AccountHolderIdentifier identifier, Text lastName, Text firstName, Text emailAddress) {
        guard(identifier.value()).againstNullOrWhitespace(ValidationMessages.ACCOUNT_ID_EMPTY);
        guard(lastName).againstNullOrWhitespace(ValidationMessages.ACCOUNT_HOLDER_LAST_NAME_EMPTY);
        guard(firstName).againstNullOrWhitespace(ValidationMessages.ACCOUNT_HOLDER_FIRST_NAME_EMPTY);
        return new AccountHolder(identifier, lastName, firstName, Email.of(emailAddress));
    }

    public static AccountHolder of(Text lastName, Text firstName, Text emailAddress) {
        return new AccountHolder(lastName, firstName, Email.of(emailAddress));
    }

    public static AccountHolder of(Text lastName, Text firstName) {
        return new AccountHolder(lastName, firstName);
    }
}
