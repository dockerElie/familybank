package nesous.digital.services.familyBank.boundedContexts.shareKernel.commons;


import nesous.digital.services.familyBank.boundedContexts.shareKernel.Email;

public class EmailGuard extends BaseGuard<Email> {

    public EmailGuard(Email value) {
        super(value);
    }

    public void againstInvalidEmail(String message) {
        against(value::isInvalidEmailAddress, message);
    }
}
