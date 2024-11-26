package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.api.EmailSender;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model.Mail;
import org.easymock.Capture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.stream.Stream;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmailSenderTest {

    private final JavaMailSender javaMailSender = createMock(JavaMailSender.class);
    private final MimeMessage mimeMessageMock = createMock(MimeMessage.class);


    private EmailSender emailSender;

    private static Stream<Arguments> testSendEmail_whenGivingAMail_thenSend() {

        Mail mail = Mail.builder()
                .mailSubject("mailSubject")
                .mailFrom("mailFrom")
                .mailRecipients(Collections.singletonList("email@address.com"))
                .mailContent("mailContent")
                .build();

        return Stream.of(Arguments.of(mail));
    }

    @BeforeEach
    public void setUp() {
        emailSender = new EmailSender(javaMailSender);
    }
    @ParameterizedTest
    @MethodSource
    @DisplayName("send Email")
    public void testSendEmail_whenGivingAMail_thenSend(Mail mail) throws MessagingException {

        // Arrange
        Capture<MimeMessage> mimeMessageCapture = newCapture();

        expect(javaMailSender.createMimeMessage()).andDelegateTo(new JavaMailSenderImpl());

        javaMailSender.send(capture(mimeMessageCapture));
        expectLastCall().andVoid().anyTimes();

        // Act
        replay(javaMailSender, mimeMessageMock);
        emailSender.sendEmail(mail);

        // Assert
        verify(javaMailSender);
        MimeMessage sentMimeMessage = mimeMessageCapture.getValue();
        assertEquals(mail.getMailSubject(), sentMimeMessage.getSubject());
        assertEquals(mail.getMailRecipients().size(), sentMimeMessage.getRecipients(Message.RecipientType.TO).length);
    }
}
