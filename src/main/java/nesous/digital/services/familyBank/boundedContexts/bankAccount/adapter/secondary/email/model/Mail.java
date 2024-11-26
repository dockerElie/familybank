package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Mail {

    private String mailFrom;
    private List<String> mailRecipients;
    private List<String> mailRecipientsCc;
    private List<String> mailRecipientsBcc;
    private String mailContent;
    private String mailSubject;
}
