package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.api;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model.Mail;

import javax.mail.MessagingException;

public interface EmailService {

    void sendEmail(Mail mail) throws MessagingException;
}
