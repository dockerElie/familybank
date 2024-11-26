package nesous.digital.services.familyBank.boundedContexts.shareKernel;

import nesous.digital.services.familyBank.boundedContexts.shareKernel.exceptions.ValidationMessages;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.utils.Guard.guard;

public record Email(Text emailAddress) {

    private static final String EMAIL_REGEX = "^(.+)@(\\S+)$";
    public static Email of(Text emailAddress) {

        Email email = new Email(emailAddress);
        guard(emailAddress).againstNullOrWhitespace(ValidationMessages.ACCOUNT_HOLDER_EMAIL_EMPTY);
        guard(email).againstInvalidEmail(ValidationMessages.INVALID_EMAIL_ADDRESS);
        return email;
    }
    // Validate email address using a simple regular expression for checking the presence of the @
    public boolean isInvalidEmailAddress() {
        return !emailAddress().value().matches(EMAIL_REGEX);
    }
}
