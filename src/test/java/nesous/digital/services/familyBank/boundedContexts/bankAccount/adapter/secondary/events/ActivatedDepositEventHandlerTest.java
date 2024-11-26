package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.events;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.api.EmailService;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model.Mail;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolder;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolderIdentifier;
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
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.verify;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class ActivatedDepositEventHandlerTest {

    private ActivateDepositRequestedEventHandler activateDepositRequestedEventHandler;

    MessageSource message = createMock(MessageSource.class);
    EmailService emailService = createMock(EmailService.class);

    ITemplateEngine thymeleafTemplateEngine = createMock(ITemplateEngine.class);

    private static Stream<Arguments> testHandleActivateDepositRequestedEvent_GivenActivateDepositRequestedEvent_thenSendEmail() {

        String userId = "1L";
        Text lastName  = new Text("NONO");
        Text firstName = new Text("ELIE MICHEL");
        Text email = new Text("eliewear1@gmail.com");
        AccountHolder accountHolder = AccountHolder.of(new AccountHolderIdentifier(userId), lastName, firstName, email);
        LocalDate localDate = LocalDate.of(2023, 1, 25);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        ActivateDepositRequested activateDepositRequested = ActivateDepositRequested.of(
                UUID.randomUUID(), date, accountHolder.email().emailAddress().value());
        return Stream.of(Arguments.of(activateDepositRequested));
    }

    @BeforeEach
    public void setup() {
        activateDepositRequestedEventHandler = new ActivateDepositRequestedEventHandler(message, emailService, thymeleafTemplateEngine);
        ReflectionTestUtils.setField(activateDepositRequestedEventHandler, "mailFrom", "familbank");
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("handle activate deposit requested event")
    public void testHandleActivateDepositRequestedEvent_GivenActivateDepositRequestedEvent_thenSendEmail(
            ActivateDepositRequested activateDepositRequested) throws MessagingException {

        // Arrange
        // Set the mock in the LocaleContextHolder
        LocaleContextHolder.setDefaultLocale(Locale.FRANCE);

        Capture<Mail> mailCapture = newCapture();
        String mailSubject = "Family Bank APP - Deposit Activation request";
        String title = "Deposit Activation request";
        String greetings = "Dear account manager";
        String body = "Some users requested you to approve their demand about reactivating some deposits";
        String body1 = "Please have a look";
        String thanks = "Do not reply to this email";
        String expectedContent = "<html>" +
                "<head>" +
                "<title>"+title+"</title>" +
                "</head>" +
                "<body>" +
                "<div>" +
                "<h1><span>"+greetings+"</span></h1>"+
                "</div>" +
                "<p>"+body+"</p>"+
                "<p>"+body1+"</p>"+
                "<p>"+thanks+"</p>"+
                "<div><br/>"+
                "<p>Family Bank Nesous Digital</p>"+
                "</div>"+
                "</body>"+
                "</html>";

        expect(message.getMessage(ActivateDepositRequestedEventHandler.MAIL_SUBJECT_KEY, null, Locale.FRANCE)).andReturn(mailSubject);

        expect(message.getMessage(ActivateDepositRequestedEventHandler.MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_TITLE_KEY,
                null, Locale.FRANCE)).andReturn(title);

        expect(message.getMessage(ActivateDepositRequestedEventHandler.MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_GREETINGS_KEY, null,
                Locale.FRANCE)).andReturn(greetings);

        expect(message.getMessage(ActivateDepositRequestedEventHandler.MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_BODY_KEY, null,
                Locale.FRANCE)).andReturn(body);

        expect(message.getMessage(ActivateDepositRequestedEventHandler.MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_BODY1_KEY,
                null, Locale.FRANCE)).andReturn(body1);

        expect(message.getMessage(ActivateDepositRequestedEventHandler.MESSAGE_DEPOSIT_REQUEST_TO_ACTIVATE_THANKS,
                null, Locale.FRANCE)).andReturn(thanks);

        expect(thymeleafTemplateEngine.process(isA(String.class), isA(Context.class)))
                .andReturn(expectedContent);

        emailService.sendEmail(capture(mailCapture));
        expectLastCall().andVoid().anyTimes();

        // Act
        replay(message, emailService, thymeleafTemplateEngine);
        activateDepositRequestedEventHandler.handleActivateDepositRequestedEvent(activateDepositRequested);

        // Assert
        Mail captureMail = mailCapture.getValue();
        assertEquals("Mail Subject", mailSubject , captureMail.getMailSubject());
        assertEquals("Mail From", "familbank", captureMail.getMailFrom());
        assertEquals("Mail To", "eliewear1@gmail.com", captureMail.getMailRecipients().get(0));
        assertEquals("Mail Content", expectedContent, captureMail.getMailContent());

        verify(message, emailService, thymeleafTemplateEngine);
    }
}
