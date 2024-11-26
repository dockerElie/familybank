package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.events;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.api.EmailService;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model.Mail;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
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
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.verify;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class DepositReactivatedEventHandlerTest {

    private DepositReactivatedEventHandler depositReactivatedEventHandler;

    MessageSource message = createMock(MessageSource.class);
    EmailService emailService = createMock(EmailService.class);

    ITemplateEngine thymeleafTemplateEngine = createMock(ITemplateEngine.class);

    @BeforeEach
    public void setup() {
        depositReactivatedEventHandler = new DepositReactivatedEventHandler(message, emailService, thymeleafTemplateEngine);
        ReflectionTestUtils.setField(depositReactivatedEventHandler, "mailFrom", "familbank");
    }

    private static Stream<Arguments> testHandleDepositReactivatedEvent_GivenDepositReactivatedEvent_thenSendEmail() {

        String userId = "1L";
        Text lastName  = new Text("NONO");
        Text firstName = new Text("ELIE MICHEL");
        Text email = new Text("eliewear1@gmail.com");
        AccountHolder accountHolder = AccountHolder.of(new AccountHolderIdentifier(userId), lastName, firstName, email);
        LocalDate localDate = LocalDate.of(2024, 1, 25);
        java.util.Date date = java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        DepositIdentifier depositIdentifier = DepositIdentifier.generate();
        DepositName depositName = DepositName.of(new Text("deposit_name"));
        ExpirationDate expirationDate = ExpirationDate.of(new Date(1, Month.FEBRUARY, 2024));

        DepositReactivated depositReactivated = DepositReactivated.of(UUID.randomUUID(), date, accountHolder.getFullName(),
                accountHolder.email().emailAddress(), depositIdentifier, depositName.text(), expirationDate.date());
        return Stream.of(Arguments.of(depositReactivated));
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("handle deposit reactivated event")
    public void testHandleDepositReactivatedEvent_GivenDepositReactivatedEvent_thenSendEmail(
            DepositReactivated depositReactivated) throws MessagingException {

        // Arrange
        // Set the mock in the LocaleContextHolder
        LocaleContextHolder.setDefaultLocale(Locale.FRANCE);
        Object[] arg1 = depositReactivatedEventHandler.getMessageSourceArg(depositReactivated.getAccountHolder().value(),
                depositReactivated.getDepositName().value());
        Object[] arg2 = depositReactivatedEventHandler.getExpirateDateArg(depositReactivated.getExpirationDate().formattedDate());

        Capture<Mail> mailCapture = newCapture();
        String mailSubject = "Family Bank APP - Deposit reactivation";
        String title = "Deposit reactivation";
        String greetings = "Dear elie your deposit eline has been reactivated";
        String body = "You can now make a deposit.";
        String body1 = "The deposit will expire on 21/02/2025";
        String expectedContent = "<html>" +
                "<head>" +
                "<title>" + title + "</title>" +
                "</head>" +
                "<body>" +
                "<div>" +
                "<h1><span>" + greetings + "</span></h1>" +
                "</div>" +
                "<p>" + body + "</p>" +
                "<p>" + body1 + "</p>" +
                "<div><br/>" +
                "<p>Account manager</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        expect(message.getMessage(DepositReactivatedEventHandler.MAIL_SUBJECT_KEY, null, Locale.FRANCE)).andReturn(mailSubject);

        expect(message.getMessage(DepositReactivatedEventHandler.MESSAGE_DEPOSIT_REACTIVATION_TITLE_KEY,
                null, Locale.FRANCE)).andReturn(title);

        expect(message.getMessage(DepositReactivatedEventHandler.MESSAGE_DEPOSIT_REACTIVATION_GREETINGS_KEY, arg1,
                Locale.FRANCE)).andReturn(greetings);

        expect(message.getMessage(DepositReactivatedEventHandler.MESSAGE_DEPOSIT_REACTIVATION_BODY_KEY, null,
                Locale.FRANCE)).andReturn(body);

        expect(message.getMessage(DepositReactivatedEventHandler.MESSAGE_DEPOSIT_REACTIVATION_BODY1_KEY,
                arg2, Locale.FRANCE)).andReturn(body1);

        expect(thymeleafTemplateEngine.process(isA(String.class), isA(Context.class)))
                .andReturn(expectedContent);

        emailService.sendEmail(capture(mailCapture));
        expectLastCall().andVoid().anyTimes();

        // Act
        replay(message, emailService, thymeleafTemplateEngine);
        depositReactivatedEventHandler.handleDepositReactivatedEvent(depositReactivated);

        // Assert
        Mail captureMail = mailCapture.getValue();
        assertEquals("Mail Subject", mailSubject, captureMail.getMailSubject());
        assertEquals("Mail From", "familbank", captureMail.getMailFrom());
        assertEquals("Mail To", "eliewear1@gmail.com", captureMail.getMailRecipients().get(0));
        assertEquals("Mail Content", expectedContent, captureMail.getMailContent());

        verify(message, emailService, thymeleafTemplateEngine);
    }
}
