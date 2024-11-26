package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.email.model;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collections;
import java.util.Map;

public final class EmailBuilder {

    public static Mail build(ITemplateEngine thymeleafTemplateEngine, Map<String, Object> templateModel,
                             String mailSubject, String mailFrom, String emailAddress, String emailTemplate) {

        Context thymeleafContext = getThymeLeafContext();
        thymeleafContext.setVariables(templateModel);
        String mailContent = thymeleafTemplateEngine.process(emailTemplate, thymeleafContext);
        return Mail.builder()
                .mailSubject(mailSubject)
                .mailFrom(mailFrom)
                .mailRecipients(Collections.singletonList(emailAddress))
                .mailContent(mailContent)
                .build();
    }

    private static Context getThymeLeafContext() {
        return new Context();
    }
}
