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
public class ActivateDepositRequestedEventHandler {

    public static final String MAIL_SUBJECT_KEY = "deposit.activation.requested.mail.subject";
    public static final String MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_TITLE_KEY = "message.deposit.request.to.activate.title";

    public static final String MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_GREETINGS_KEY= "message.deposit.request.to.activate.greetings";

    public static final String MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_BODY_KEY = "message.deposit.request.to.activate.body";

    public static final String MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_BODY1_KEY = "message.deposit.request.to.activate.body1";

    public static final String MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_THANKS = "message.deposit.request.to.activate.thanks";

    @Value("${spring.mail.username}")
    private String mailFrom;

    private final MessageSource messages;

    private final EmailService emailService;

    private final ITemplateEngine thymeleafTemplateEngine;

    public ActivateDepositRequestedEventHandler(@Qualifier("messageSource") MessageSource messages, EmailService emailService,
                                                ITemplateEngine thymeleafTemplateEngine) {
        this.messages = messages;
        this.emailService = emailService;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    @EventListener
    public void handleActivateDepositRequestedEvent(ActivateDepositRequested activateDepositRequestedEvent) {
        try {

            String mailSubject = messages.getMessage(MAIL_SUBJECT_KEY, null, LocaleContextHolder.getLocale());

            Map<String, Object> templateModel = new HashMap<>();

            Locale locale = LocaleContextHolder.getLocale();

            templateModel.put(MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_TITLE_KEY,
                    messages.getMessage(MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_TITLE_KEY, null, locale));

            templateModel.put(MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_GREETINGS_KEY,
                    messages.getMessage(MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_GREETINGS_KEY, null, locale));

            templateModel.put(MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_BODY_KEY,
                    messages.getMessage(MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_BODY_KEY, null, locale));

            templateModel.put(MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_BODY1_KEY,
                    messages.getMessage(MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_BODY1_KEY, null, locale));

            templateModel.put(MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_THANKS,
                    messages.getMessage(MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_THANKS, null, locale));

            Mail mail = EmailBuilder.build(thymeleafTemplateEngine, templateModel, mailSubject, mailFrom,
                    activateDepositRequestedEvent.getManagerEmail(), "deposit_requested.html");
            emailService.sendEmail(mail);
        } catch (MessagingException e) {
            log.error("An error occurred when sending email", e);
        }
    }
}
