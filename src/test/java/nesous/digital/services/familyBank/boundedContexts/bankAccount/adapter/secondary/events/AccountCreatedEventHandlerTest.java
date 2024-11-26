package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.events;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.api.EmailService;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model.Mail;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolder;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolderIdentifier;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;
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
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class AccountCreatedEventHandlerTest {

    private AccountCreatedEventHandler accountCreatedEventHandler;

    MessageSource message = createMock(MessageSource.class);
    EmailService emailService = createMock(EmailService.class);

    ITemplateEngine thymeleafTemplateEngine = createMock(ITemplateEngine.class);

    private static Stream<Arguments> testHandleAccountCreatedEvent_whenGivenAnAccountCreatedEvent_thenSendAnEmail() {

        String userId = "1L";
        Text lastName  = new Text("NONO");
        Text firstName = new Text("ELIE MICHEL");
        Text email = new Text("eliewear1@gmail.com");
        AccountId accountId = Account.nextAccountId();
        AccountHolder accountHolder = AccountHolder.of(new AccountHolderIdentifier(userId), lastName, firstName, email);
        LocalDate localDate = LocalDate.of(2023, 1, 25);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        AccountCreated accountCreated = AccountCreated.of(accountId, accountHolder.getFullName(), UUID.randomUUID(),
                date, accountHolder.email().emailAddress());
        return Stream.of(Arguments.of(accountCreated));
    }

    @BeforeEach
    public void setup() {
        accountCreatedEventHandler = new AccountCreatedEventHandler(message, emailService, thymeleafTemplateEngine);
        ReflectionTestUtils.setField(accountCreatedEventHandler, "mailFrom", "familbank");
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("handle account created event")
    public void testHandleAccountCreatedEvent_whenGivenAnAccountCreatedEvent_thenSendAnEmail(AccountCreated accountCreated) throws MessagingException {

        // Arrange
        Capture<Mail> mailCapture = newCapture();
        String mailSubject = "Deposit Bank account created";
        String messageBankAccountCreationTitle = "Bank account creation";
        String messageBankAccountCreationGreetings = "Hello NONO ELIE MICHEL";
        String messageBankAccountCreationBody = "Your deposit bank account for the application FAMILY BANK has been created.";
        String messageBankAccountCreationCredentialKeys = "You can now access the application using your credentials you already received by mail.";
        String messageBankAccountCreationCredentialThanks = "Best Regards";
        String expectedContent = "<html>" +
                "<head>" +
                "<title>"+messageBankAccountCreationTitle+"</title>" +
                "</head>" +
                "<body>" +
                "<div>" +
                "<h1><span>"+messageBankAccountCreationGreetings+"</span></h1>"+
                "</div>" +
                "<p>"+messageBankAccountCreationBody+"</p>"+
                "<p>"+messageBankAccountCreationCredentialKeys+"</p>"+
                "<div>"+
                "<p>"+messageBankAccountCreationCredentialThanks+"</p>"+
                "<p>FAMILY BANK APP</p>"+
                "</div>"+
                "</body>"+
                "</html>";

        // Set the mock in the LocaleContextHolder
        LocaleContextHolder.setDefaultLocale(Locale.FRANCE);
        Object[] args = accountCreatedEventHandler.getMessageSourceArg(accountCreated.getAccountHolder().value());

        expect(message.getMessage(AccountCreatedEventHandler.MAIL_SUBJECT_KEY, null, Locale.FRANCE)).andReturn(mailSubject);

        expect(message.getMessage(AccountCreatedEventHandler.MESSAGE_BANK_ACCOUNT_CREATION_TITLE_KEY,
                null, Locale.FRANCE)).andReturn(messageBankAccountCreationTitle);

        expect(message.getMessage(AccountCreatedEventHandler.MESSAGE_BANK_ACCOUNT_CREATION_GREETINGS_KEY,args,
                Locale.FRANCE)).andReturn(messageBankAccountCreationGreetings);

        expect(message.getMessage(AccountCreatedEventHandler.MESSAGE_BANK_ACCOUNT_CREATION_BODY_KEY, null,
                Locale.FRANCE)).andReturn(messageBankAccountCreationBody);

        expect(message.getMessage(AccountCreatedEventHandler.MESSAGE_BANK_ACCOUNT_CREATION_CREDENTIALS_KEY, null,
                Locale.FRANCE)).andReturn(messageBankAccountCreationCredentialKeys);

        expect(message.getMessage(AccountCreatedEventHandler.MESSAGE_BANK_ACCOUNT_CREATION_CREDENTIALS_THANKS, null,
                Locale.FRANCE)).andReturn(messageBankAccountCreationCredentialThanks);

        expect(thymeleafTemplateEngine.process(isA(String.class), isA(Context.class)))
                .andReturn(expectedContent);

        emailService.sendEmail(capture(mailCapture));
        expectLastCall().andVoid().anyTimes();

        // Act
        replay(message, emailService, thymeleafTemplateEngine);
        accountCreatedEventHandler.handleAccountCreatedEvent(accountCreated);

        // Assert
        Mail captureMail = mailCapture.getValue();
        assertEquals("Mail Subject", "Deposit Bank account created" , captureMail.getMailSubject());
        assertEquals("Mail From", "familbank", captureMail.getMailFrom());
        assertEquals("Mail To", "eliewear1@gmail.com", captureMail.getMailRecipients().get(0));
        assertEquals("Mail Content", expectedContent, captureMail.getMailContent());
        verify(message, emailService, thymeleafTemplateEngine);
    }
}
