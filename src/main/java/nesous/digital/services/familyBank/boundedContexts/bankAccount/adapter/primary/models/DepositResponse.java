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
public class DepositResponse {

    private String identifier;
    private String name;
    private Date date;
    private double amount;
    private String status;
    private Date userExpirationDate;
    private Date expirationDate;
    private String description;
}
