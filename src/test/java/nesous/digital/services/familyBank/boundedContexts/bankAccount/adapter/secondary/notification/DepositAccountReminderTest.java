package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.notification;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.api.EmailService;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model.Mail;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.DateTimeComponent;
import org.easymock.Capture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.ITemplateEngine;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.notification.DepositAccountReminder.*;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.ACTIVATED;
import static org.easymock.EasyMock.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import org.thymeleaf.context.Context;
import javax.mail.MessagingException;


public class DepositAccountReminderTest {

    private DepositAccountReminder depositAccountReminder;

    DepositProvider depositProvider = createMock(DepositProvider.class);
    AccountProvider accountProvider = createMock(AccountProvider.class);
    ITemplateEngine thymeleafTemplateEngine = createMock(ITemplateEngine.class);
    MessageSource message = createMock(MessageSource.class);
    EmailService emailService = createMock(EmailService.class);

    DateTimeComponent dateTimeComponent = createMock(DateTimeComponent.class);

    @BeforeEach
    public void setup() {
        depositAccountReminder = new DepositAccountReminder(depositProvider, accountProvider, thymeleafTemplateEngine,
                message, emailService, dateTimeComponent);
        ReflectionTestUtils.setField(depositAccountReminder, "mailFrom", "familbank");
    }

    @Test
    public void testSendReminder_thenSendDepositReminder() throws MessagingException, InterruptedException {

        // Arrange
        LocaleContextHolder.setDefaultLocale(Locale.FRANCE);
        Capture<Mail> mailCapture = newCapture();
        AccountId accountId = Account.nextAccountId();
        AccountHolderIdentifier accountHolderIdentifier = new AccountHolderIdentifier("ere-eret-ved-1");
        Text lastName = new Text("NONO");
        Text firstName = new Text("ELIE MICHEL");
        Text email = new Text("elie@rer.com");
        Date depositDate = new Date(3, Month.JANUARY, 2023);

        String messageDepositAccountReminderTitle = "Deposit account reminder";
        String messageDepositAccountReminderGreetings = "Dear NONO ELIE MICHEL";
        String messageDepositAccountReminderBody = "A kindly reminder to make a deposit before the expiration date '01-01-2024'";

        AccountHolder accountHolder = AccountHolder.of(accountHolderIdentifier, lastName, firstName, email);
        DepositReminderDate depositReminderDate = new DepositReminderDate(depositDate.plusDays(5));

        Deposit deposit = depositBuilder()
                .withIdentifier(DepositIdentifier.generate())
                .withName(DepositName.of(new Text("deposit name")))
                .withDate(depositDate)
                .withExpirationDate(ExpirationDate.of(new Date(1, Month.JANUARY, 2024)))
                .withDepositReminderDate(depositReminderDate)
                .withDescription(new Description(new Text("deposit name")))
                .withMoney(DepositMoney.of(Money.of(100)))
                .withStatus(ACTIVATED)
                .withAccountId(accountId)
                .build();
        Account account = Account.builder()
                .accountId(accountId)
                .accountHolder(accountHolder)
                .deposit(deposit)
                .build();
        String mailSubject = "Family Bank APP - Deposit account expiration";
        Object[] arg1 = depositAccountReminder.getMessageSourceArg(accountHolder.getFullName().value());
        Object[] arg2 = depositAccountReminder.getMessageSourceArg(account.getDepositExpirationDate().date().formattedDate());

        String expectedContent = "<html>" +
                "<head>" +
                "<title>"+messageDepositAccountReminderTitle+"</title>" +
                "</head>" +
                "<body>" +
                "<div>" +
                "<h1><span>"+messageDepositAccountReminderGreetings+"</span></h1>"+
                "</div>" +
                "<p>"+messageDepositAccountReminderBody+"</p>"+
                "<div><br/>"+
                "<p>Account manager</p>"+
                "</div>"+
                "</body>"+
                "</html>";

        expect(dateTimeComponent.now()).andReturn(LocalDate.of(2023, Month.JANUARY, 8));
        expect(message.getMessage(MAIL_SUBJECT_DEPOSIT_EXPIRATION_KEY, null, Locale.FRANCE)).andReturn(mailSubject);
        expect(depositProvider.list()).andReturn(Collections.singletonList(deposit));
        expect(accountProvider.withDeposit(isA(Deposit.class))).andReturn(account);
        expect(message.getMessage(MESSAGE_DEPOSIT_ACCOUNT_REMINDER_TITLE_KEY,
                null, Locale.FRANCE)).andReturn(messageDepositAccountReminderTitle);

        expect(message.getMessage(MESSAGE_DEPOSIT_ACCOUNT_REMINDER_GREETINGS_KEY,
                arg1, Locale.FRANCE)).andReturn(messageDepositAccountReminderGreetings);

        expect(message.getMessage(MESSAGE_DEPOSIT_ACCOUNT_REMINDER_BODY_KEY,
                arg2, Locale.FRANCE)).andReturn(messageDepositAccountReminderBody);

        expect(thymeleafTemplateEngine.process(isA(String.class), isA(Context.class)))
                .andReturn(expectedContent);

        emailService.sendEmail(capture(mailCapture));
        expectLastCall().andVoid().anyTimes();

        accountProvider.update(isA(Account.class));
        expectLastCall();

        // Act
        replay(message, emailService, thymeleafTemplateEngine, accountProvider, dateTimeComponent, depositProvider);
        depositAccountReminder.sendReminder();

        // Assert
        Mail captureMail = mailCapture.getValue();
        assertEquals("Mail Subject", mailSubject , captureMail.getMailSubject());
        assertEquals("Mail From", "familbank", captureMail.getMailFrom());
        assertEquals("Mail To", "elie@rer.com", captureMail.getMailRecipients().get(0));
        assertEquals("Mail Content", expectedContent, captureMail.getMailContent());

        verify(message, emailService, thymeleafTemplateEngine, accountProvider, dateTimeComponent);
    }

    @Test
    public void testSendReminder_WhenSettingUserExpirationDate_thenSendDepositReminder() throws InterruptedException {

        // Arrange
        AccountId accountId = Account.nextAccountId();
        AccountHolderIdentifier accountHolderIdentifier = new AccountHolderIdentifier("ere-eret-ved-1");
        Text lastName = new Text("NONO");
        Text firstName = new Text("ELIE MICHEL");
        Text email = new Text("elie@rer.com");
        Date depositDate = new Date(3, Month.JANUARY, 2023);

        AccountHolder accountHolder = AccountHolder.of(accountHolderIdentifier, lastName, firstName, email);
        DepositReminderDate depositReminderDate = new DepositReminderDate(depositDate.plusDays(5));

        Deposit deposit = depositBuilder()
                .withIdentifier(DepositIdentifier.generate())
                .withName(DepositName.of(new Text("deposit name")))
                .withDate(depositDate)
                .withExpirationDate(ExpirationDate.of(new Date(1, Month.JANUARY, 2024)))
                .withUserExpirationDate(new UserExpirationDate(new Date(1, Month.JANUARY, 2024)))
                .withDepositReminderDate(depositReminderDate)
                .withDescription(new Description(new Text("deposit name")))
                .withMoney(DepositMoney.of(Money.of(100)))
                .withStatus(ACTIVATED)
                .withAccountId(accountId)
                .build();

        Deposit deposit1 = depositBuilder()
                .withIdentifier(DepositIdentifier.generate())
                .withName(DepositName.of(new Text("deposit name1")))
                .withDate(depositDate)
                .withExpirationDate(ExpirationDate.of(new Date(3, Month.JANUARY, 2024)))
                .withUserExpirationDate(new UserExpirationDate(new Date(1, Month.JANUARY, 2024).plusDays(10)))
                .withDepositReminderDate(depositReminderDate)
                .withDescription(new Description(new Text("deposit name1")))
                .withMoney(DepositMoney.of(Money.of(200)))
                .withStatus(ACTIVATED)
                .withAccountId(accountId)
                .build();

        List<Deposit> depositList = new ArrayList<>();
        depositList.add(deposit);
        depositList.add(deposit1);

        Account account = Account.builder()
                .accountId(accountId)
                .accountHolder(accountHolder)
                .deposit(deposit)
                .build();

        expect(depositProvider.list()).andReturn(depositList);
        expect(accountProvider.withDeposit(isA(Deposit.class))).andReturn(account).anyTimes();
        expect(depositProvider.searchDepositByMaximumUserExpirationDate(isA(AccountId.class))).andReturn(deposit1).anyTimes();

        accountProvider.update(isA(Account.class));
        expectLastCall().anyTimes();

        // Act
        replay(accountProvider, depositProvider);
        depositAccountReminder.sendReminder();

        // Assert
        verify(accountProvider, depositProvider);

    }
}
