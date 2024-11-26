package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.api;

import lombok.RequiredArgsConstructor;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model.Mail;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailSender implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(Mail mail) throws MessagingException {
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();

        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(mail.getMailRecipients().toArray(new String[0]));
        helper.setSubject(mail.getMailSubject());
        helper.setText(mail.getMailContent(), true);
        this.mailSender.send(mimeMessage);
    }
}
