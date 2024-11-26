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
public class DepositCommandRequest {

    private String identifier;
    private String name;
    private String description;
    private Date date;
    private Date expirationDate;
    private Date userExpirationDate;
    private Double money;
    private String reason;
}
