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
public class DepositReactivatedEventHandler {

    public static final String MAIL_SUBJECT_KEY = "deposit.reactivation.mail.subject";
    public static final String MESSAGE_DEPOSIT_REACTIVATION_TITLE_KEY = "message.deposit.reactivation.title";

    public static final String MESSAGE_DEPOSIT_REACTIVATION_GREETINGS_KEY= "message.deposit.reactivation.greetings";

    public static final String MESSAGE_DEPOSIT_REACTIVATION_BODY_KEY = "message.deposit.reactivation.body";

    public static final String MESSAGE_DEPOSIT_REACTIVATION_BODY1_KEY = "message.deposit.reactivation.body1";

    @Value("${spring.mail.username}")
    private String mailFrom;

    private final MessageSource messages;

    private final EmailService emailService;

    private final ITemplateEngine thymeleafTemplateEngine;

    public DepositReactivatedEventHandler(@Qualifier("messageSource") MessageSource messages, EmailService emailService, ITemplateEngine thymeleafTemplateEngine) {
        this.messages = messages;
        this.emailService = emailService;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    @EventListener
    public void handleDepositReactivatedEvent(DepositReactivated depositReactivated) {
        try {
            String mailSubject = messages.getMessage(MAIL_SUBJECT_KEY, null, LocaleContextHolder.getLocale());

            Map<String, Object> templateModel = new HashMap<>();

            Locale locale = LocaleContextHolder.getLocale();

            templateModel.put(MESSAGE_DEPOSIT_REACTIVATION_TITLE_KEY,
                    messages.getMessage(MESSAGE_DEPOSIT_REACTIVATION_TITLE_KEY, null, locale));

            templateModel.put(
                    MESSAGE_DEPOSIT_REACTIVATION_GREETINGS_KEY,
                    messages.getMessage(MESSAGE_DEPOSIT_REACTIVATION_GREETINGS_KEY,
                            getMessageSourceArg(depositReactivated.getAccountHolder().value(),
                                    depositReactivated.getDepositName().value()), locale));

            templateModel.put(MESSAGE_DEPOSIT_REACTIVATION_BODY_KEY,
                    messages.getMessage(MESSAGE_DEPOSIT_REACTIVATION_BODY_KEY, null, locale));

            templateModel.put(MESSAGE_DEPOSIT_REACTIVATION_BODY1_KEY,
                    messages.getMessage(MESSAGE_DEPOSIT_REACTIVATION_BODY1_KEY,
                            getExpirateDateArg(depositReactivated.getExpirationDate().formattedDate()), locale));

            Mail mail = EmailBuilder.build(thymeleafTemplateEngine, templateModel, mailSubject, mailFrom,
                    depositReactivated.getAccountHolderEmailAddress().value(), "deposit_reactivated.html");
            emailService.sendEmail(mail);
        } catch (MessagingException e) {
            log.error("An error occurred when sending email", e);
        }
    }

    Object[] getMessageSourceArg(String params, String param1) {
        return new Object[]{params, param1};
    }

    Object[] getExpirateDateArg(String params) {
        return new Object[]{params};
    }
}
