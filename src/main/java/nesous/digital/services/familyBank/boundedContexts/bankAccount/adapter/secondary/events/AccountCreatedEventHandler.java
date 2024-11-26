package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.events;

import lombok.extern.slf4j.Slf4j;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.api.EmailService;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model.EmailBuilder;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model.Mail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class AccountCreatedEventHandler {

    public static final String MAIL_SUBJECT_KEY = "account.created.mail.subject";
    public static final String MESSAGE_BANK_ACCOUNT_CREATION_TITLE_KEY = "message.bank.account.creation.title";
    public static final String MESSAGE_BANK_ACCOUNT_CREATION_GREETINGS_KEY= "message.bank.account.creation.greetings";
    public static final String MESSAGE_BANK_ACCOUNT_CREATION_BODY_KEY = "message.bank.account.creation.body";
    public static final String MESSAGE_BANK_ACCOUNT_CREATION_CREDENTIALS_KEY = "message.bank.account.creation.credentials";
    public static final String MESSAGE_BANK_ACCOUNT_CREATION_CREDENTIALS_THANKS = "message.bank.account.creation.credentials.thanks";

    @Value("${spring.mail.username}")
    private String mailFrom;

    private final MessageSource messages;

    private final EmailService emailService;

    private final ITemplateEngine thymeleafTemplateEngine;

    public AccountCreatedEventHandler(@Qualifier("messageSource") MessageSource messages,
                                      EmailService emailService, ITemplateEngine thymeleafTemplateEngine) {
        this.messages = messages;
        this.emailService = emailService;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    @EventListener
    public void handleAccountCreatedEvent(AccountCreated accountCreated) {
        try {
            System.out.println("Account created: " + accountCreated.getAccountId().accountId());

            String mailSubject = messages.getMessage(MAIL_SUBJECT_KEY, null, LocaleContextHolder.getLocale());

            Map<String, Object> templateModel = new HashMap<>();

            Locale locale = LocaleContextHolder.getLocale();

            templateModel.put(MESSAGE_BANK_ACCOUNT_CREATION_TITLE_KEY, messages.getMessage(MESSAGE_BANK_ACCOUNT_CREATION_TITLE_KEY,
                    null, locale));

            templateModel.put(MESSAGE_BANK_ACCOUNT_CREATION_GREETINGS_KEY, messages.getMessage(MESSAGE_BANK_ACCOUNT_CREATION_GREETINGS_KEY,
                    getMessageSourceArg(accountCreated.getAccountHolder().value()), locale));

            templateModel.put(MESSAGE_BANK_ACCOUNT_CREATION_BODY_KEY, messages.getMessage(MESSAGE_BANK_ACCOUNT_CREATION_BODY_KEY,
                    null, locale));

            templateModel.put(MESSAGE_BANK_ACCOUNT_CREATION_CREDENTIALS_KEY, messages.getMessage(MESSAGE_BANK_ACCOUNT_CREATION_CREDENTIALS_KEY,
                    null, locale));

            templateModel.put(MESSAGE_BANK_ACCOUNT_CREATION_CREDENTIALS_THANKS, messages.getMessage(MESSAGE_BANK_ACCOUNT_CREATION_CREDENTIALS_THANKS,
                    null, locale));

            Mail mail = EmailBuilder.build(thymeleafTemplateEngine, templateModel, mailSubject, mailFrom,
                    accountCreated.getAccountHolderEmailAddress().value(), "account_created.html");
            emailService.sendEmail(mail);
        } catch (MessagingException e) {
            log.error("An error occurred when sending email", e);
        }
    }

    public Object[] getMessageSourceArg(String params) {
        return new Object[]{params};
    }
}
