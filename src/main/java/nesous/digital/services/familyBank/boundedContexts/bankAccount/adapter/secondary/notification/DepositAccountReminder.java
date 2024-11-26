package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.notification;

import lombok.extern.slf4j.Slf4j;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.api.EmailService;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model.EmailBuilder;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model.Mail;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Date;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.DateConversion;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.DateTimeComponent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.ACTIVATED;

@Slf4j
@Component
public class DepositAccountReminder {

    public static final String MAIL_SUBJECT_DEPOSIT_EXPIRATION_KEY = "deposit.account.expiration.mail.subject";
    public static final String MESSAGE_DEPOSIT_ACCOUNT_REMINDER_TITLE_KEY = "message.deposit.account.reminder.title";
    public static final String MESSAGE_DEPOSIT_ACCOUNT_REMINDER_GREETINGS_KEY = "message.deposit.account.reminder.greetings";
    public static final String MESSAGE_DEPOSIT_ACCOUNT_REMINDER_BODY_KEY = "message.deposit.account.reminder.body";

    @Value("${spring.mail.username}")
    private String mailFrom;
    private final DepositProvider depositProvider;

    private final AccountProvider accountProvider;

    private final ITemplateEngine thymeleafTemplateEngine;
    private final MessageSource messages;
    private final EmailService emailService;

    private final DateTimeComponent dateTimeComponent;

    public DepositAccountReminder(DepositProvider depositProvider, AccountProvider accountProvider,
                                  ITemplateEngine thymeleafTemplateEngine,
                                  @Qualifier("messageSource") MessageSource messages, EmailService emailService,
                                  DateTimeComponent dateTimeComponent) {
        this.depositProvider = depositProvider;
        this.accountProvider = accountProvider;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
        this.messages = messages;
        this.emailService = emailService;
        this.dateTimeComponent = dateTimeComponent;
    }


    //@Scheduled(cron = "${infra.deposit.reminder.cron}")
    public void sendReminder() throws InterruptedException {
        Account trackAccountUserForLog;

        log.info("start sending deposit reminder...");

        String mailSubject = messages.getMessage(MAIL_SUBJECT_DEPOSIT_EXPIRATION_KEY, null, LocaleContextHolder.getLocale());
        List<Deposit> deposits = depositProvider.list();
        for (Deposit deposit : deposits) {
            Account account = accountProvider.withDeposit(deposit);
            trackAccountUserForLog = account;
            if (deposit.getUserExpirationDate() == null) {
                if (deposit.getStatus() == ACTIVATED && deposit.getDepositReminderDate().date()
                        .isEqual(dateTimeComponent.now()) && deposit.getDepositReminderDate().date()
                        .isBefore(deposit.getExpirationDate().date())) {
                    sendDepositReminderMail(account, trackAccountUserForLog, mailSubject);
                }
            } else {
                Deposit depositWithMaximumUserExpirationDate = depositProvider
                        .searchDepositByMaximumUserExpirationDate(account.getAccountId());
                LocalDate localDate = DateConversion.dateToLocalDate(new java.util.Date());
                Date today = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
                if (depositWithMaximumUserExpirationDate != null &&
                        depositWithMaximumUserExpirationDate.getUserExpirationDate().date().isBefore(today)) {
                    accountProvider.update(account.updateDepositReminderDate());
                }
            }

            TimeUnit.SECONDS.sleep(2);
        }
        log.info("Broadcast of {} reminder mails sent", deposits.size());
    }

    public Object[] getMessageSourceArg(String params) {
        return new Object[]{params};
    }

    private void sendDepositReminderMail(Account account, Account trackAccountUserForLog, String mailSubject) {

        try {
            Map<String, Object> templateModel = new HashMap<>();

            Locale locale = LocaleContextHolder.getLocale();
            templateModel.put(MESSAGE_DEPOSIT_ACCOUNT_REMINDER_TITLE_KEY, messages.getMessage(MESSAGE_DEPOSIT_ACCOUNT_REMINDER_TITLE_KEY,
                    null, locale));

            templateModel.put(MESSAGE_DEPOSIT_ACCOUNT_REMINDER_GREETINGS_KEY, messages.getMessage(MESSAGE_DEPOSIT_ACCOUNT_REMINDER_GREETINGS_KEY,
                    getMessageSourceArg(account.getAccountHolderName().value()), locale));

            templateModel.put(MESSAGE_DEPOSIT_ACCOUNT_REMINDER_BODY_KEY, messages.getMessage(MESSAGE_DEPOSIT_ACCOUNT_REMINDER_BODY_KEY,
                    getMessageSourceArg(account.getDepositExpirationDate().date().formattedDate()), locale));

            Mail mail = EmailBuilder.build(thymeleafTemplateEngine, templateModel, mailSubject, mailFrom,
                    account.getAccountHolderEmailAddress().value(),"deposit_account_reminder.html");
            emailService.sendEmail(mail);
            accountProvider.update(account.updateDepositReminderDate());
        } catch (MessagingException e) {
            log.error(String.format("An error occurred when sending email to %s", trackAccountUserForLog.getAccountHolderName()), e);
        }
    }
}
