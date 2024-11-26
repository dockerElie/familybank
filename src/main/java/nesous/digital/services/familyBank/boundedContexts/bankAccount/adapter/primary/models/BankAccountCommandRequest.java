package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountCommandRequest {

    private String userId;
    private String lastName;
    private String firstName;
    private String email;

}
