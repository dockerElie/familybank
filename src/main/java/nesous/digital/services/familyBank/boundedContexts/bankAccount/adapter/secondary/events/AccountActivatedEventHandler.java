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
public class AccountActivatedEventHandler {

    public static final String MAIL_SUBJECT_KEY = "deposit.account.activated.mail.subject";

    public static final String MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_TITLE_KEY = "message.deposit.account.activation.title";
    public static final String MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_GREETINGS_KEY= "message.deposit.account.activation.greetings";
    public static final String MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_BODY_KEY = "message.deposit.account.activation.body";
    public static final String MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_BODY_1_KEY = "message.deposit.account.activation.body1";

    @Value("${spring.mail.username}")
    private String mailFrom;

    private final MessageSource messages;

    private final EmailService emailService;

    private final ITemplateEngine thymeleafTemplateEngine;

    public AccountActivatedEventHandler(@Qualifier("messageSource") MessageSource messages,
                                        EmailService emailService,
                                        ITemplateEngine thymeleafTemplateEngine) {
        this.messages = messages;
        this.emailService = emailService;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    @EventListener
    public void handleDepositAccountActivatedEvent(AccountActivated accountActivated) {

        try {
            Mail depositAccountActivatedMail = sendDepositAccountActivatedEmail(accountActivated);
            emailService.sendEmail(depositAccountActivatedMail);
        } catch (MessagingException e) {
            log.error("An error occurred when sending email", e);
        }
    }

    private Mail sendDepositAccountActivatedEmail(AccountActivated accountActivated) {

        String mailSubject = messages.getMessage(MAIL_SUBJECT_KEY, null, LocaleContextHolder.getLocale());

        Map<String, Object> templateModel = new HashMap<>();

        Locale locale = LocaleContextHolder.getLocale();

        templateModel.put(MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_TITLE_KEY, messages.getMessage(MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_TITLE_KEY,
                null, locale));

        templateModel.put(MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_GREETINGS_KEY, messages.getMessage(MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_GREETINGS_KEY,
                getMessageSourceArg(accountActivated.getAccountHolder().value()), locale));

        templateModel.put(MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_BODY_KEY, messages.getMessage(MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_BODY_KEY,
                getMessageSourceArg(accountActivated.getExpirationDate().formattedDate()), locale));

        templateModel.put(MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_BODY_1_KEY, messages.getMessage(MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_BODY_1_KEY,
                null, locale));

        return EmailBuilder.build(thymeleafTemplateEngine, templateModel, mailSubject, mailFrom,
                accountActivated.getAccountHolderEmailAddress().value(), "deposit_account_activated.html");
    }

    public Object[] getMessageSourceArg(String params) {
        return new Object[]{params};
    }
}
