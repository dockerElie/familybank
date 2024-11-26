package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DepositCommandResponse {

    private String identifier;
    private String name;
    private String description;
    private Date expirationDate;
    private Date date;
    private double money;
    private String status;
    private Date userExpirationDate;
    private Date depositReminderDate;
    private String reason;
    private String accountId;
}
