package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models;

import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountCommandResponse {

    private String accountId;
    private String fullName;
    private String accountHolderIdentifier;
    private DepositCommandResponse deposit;
}
