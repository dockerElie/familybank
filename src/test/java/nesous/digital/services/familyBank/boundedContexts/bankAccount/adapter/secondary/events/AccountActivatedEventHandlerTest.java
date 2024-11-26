package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.events;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.api.EmailService;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model.Mail;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolder;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolderIdentifier;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.DepositIdentifier;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import org.easymock.Capture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

import static org.easymock.EasyMock.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class AccountActivatedEventHandlerTest {

    private AccountActivatedEventHandler accountActivatedEventHandler;

    MessageSource message = createMock(MessageSource.class);
    EmailService emailService = createMock(EmailService.class);

    ITemplateEngine thymeleafTemplateEngine = createMock(ITemplateEngine.class);

    private static Stream<Arguments> testHandleDepositAccountActivatedEvent_GivenDepositAccountActivatedEvent_thenSendEmail() {

        String userId = "1L";
        Text lastName  = new Text("NONO");
        Text firstName = new Text("ELIE MICHEL");
        Text email = new Text("eliewear1@gmail.com");
        AccountHolder accountHolder = AccountHolder.of(new AccountHolderIdentifier(userId), lastName, firstName, email);
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Date expirationDate =
                new nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Date(
                        25, Month.JANUARY, 2023);
        LocalDate localDate = LocalDate.of(2023, 1, 25);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        AccountActivated accountActivated = AccountActivated.of(
                UUID.randomUUID(), date, accountHolder.getFullName(), accountHolder.email().emailAddress(),
                depositIdentifier, expirationDate);
        return Stream.of(Arguments.of(accountActivated));
    }

    @BeforeEach
    public void setup() {
        accountActivatedEventHandler = new AccountActivatedEventHandler(message, emailService, thymeleafTemplateEngine);
        ReflectionTestUtils.setField(accountActivatedEventHandler, "mailFrom", "familbank");
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("handle deposit account activated event")
    public void testHandleDepositAccountActivatedEvent_GivenDepositAccountActivatedEvent_thenSendEmail(AccountActivated accountActivated) throws MessagingException {

        // Arrange
        // Set the mock in the LocaleContextHolder
        LocaleContextHolder.setDefaultLocale(Locale.FRANCE);
        Object[] arg1 = accountActivatedEventHandler.getMessageSourceArg(accountActivated.getAccountHolder().value());
        Object[] arg2 = accountActivatedEventHandler.getMessageSourceArg(accountActivated.getExpirationDate().formattedDate());

        Capture<Mail> mailCapture = newCapture();
        String mailSubject = "Family Bank APP - Deposit account activation";
        String messageDepositAccountActivationTitle = "Deposit account activation";
        String messageDepositAccountActivationGreetings = "Dear NONO ELIE MICHEL";
        String messageDepositAccountActivationBody = "A deposit on your bank account has been activated and will expire on '25-01-2023'";
        String messageDepositAccountActivationBody1 = "You can now make a deposit.";
        String expectedContent = "<html>" +
                "<head>" +
                "<title>"+messageDepositAccountActivationTitle+"</title>" +
                "</head>" +
                "<body>" +
                "<div>" +
                "<h1><span>"+messageDepositAccountActivationGreetings+"</span></h1>"+
                "</div>" +
                "<p>"+messageDepositAccountActivationBody+"</p>"+
                "<p>"+messageDepositAccountActivationBody1+"</p>"+
                "<div><br/>"+
                "<p>Account manager</p>"+
                "</div>"+
                "</body>"+
                "</html>";

        expect(message.getMessage(AccountActivatedEventHandler.MAIL_SUBJECT_KEY, null, Locale.FRANCE)).andReturn(mailSubject);

        expect(message.getMessage(AccountActivatedEventHandler.MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_TITLE_KEY,
                null, Locale.FRANCE)).andReturn(messageDepositAccountActivationTitle);

        expect(message.getMessage(AccountActivatedEventHandler.MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_GREETINGS_KEY, arg1,
                Locale.FRANCE)).andReturn(messageDepositAccountActivationGreetings);

        expect(message.getMessage(AccountActivatedEventHandler.MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_BODY_KEY, arg2,
                Locale.FRANCE)).andReturn(messageDepositAccountActivationBody);

        expect(message.getMessage(AccountActivatedEventHandler.MESSAGE_DEPOSIT_ACCOUNT_ACTIVATION_BODY_1_KEY,
                null, Locale.FRANCE)).andReturn(messageDepositAccountActivationBody1);

        expect(thymeleafTemplateEngine.process(isA(String.class), isA(Context.class)))
                .andReturn(expectedContent);

        emailService.sendEmail(capture(mailCapture));
        expectLastCall().andVoid().anyTimes();

        // Act
        replay(message, emailService, thymeleafTemplateEngine);
        accountActivatedEventHandler.handleDepositAccountActivatedEvent(accountActivated);

        // Assert
        Mail captureMail = mailCapture.getValue();
        assertEquals("Mail Subject", mailSubject , captureMail.getMailSubject());
        assertEquals("Mail From", "familbank", captureMail.getMailFrom());
        assertEquals("Mail To", "eliewear1@gmail.com", captureMail.getMailRecipients().get(0));
        assertEquals("Mail Content", expectedContent, captureMail.getMailContent());

        verify(message, emailService, thymeleafTemplateEngine);
    }
}
