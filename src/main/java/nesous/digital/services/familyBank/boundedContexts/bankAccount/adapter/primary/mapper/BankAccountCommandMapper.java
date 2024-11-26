package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.mapper;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models.BankAccountCommandRequest;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models.BankAccountCommandResponse;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models.DepositCommandResponse;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolder;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountHolderIdentifier;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.BankAccountCommand;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BankAccountCommandMapper {

    private final DepositCommandMapper depositCommandMapper;

    public BankAccountCommandMapper(DepositCommandMapper depositCommandMapper) {
        this.depositCommandMapper = depositCommandMapper;
    }

    public List<BankAccountCommand> mapToBankAccountCommand(List<BankAccountCommandRequest> requestList) {
        return requestList.stream()
                .map(this::mapToBankAccountCommand)
                .toList();
    }

    public BankAccountCommandResponse mapToBankAccountCommandResponse(Account account) {
        BankAccountCommandResponse bankAccountCommandResponse = new BankAccountCommandResponse();
        bankAccountCommandResponse.setAccountHolderIdentifier(account.getAccountHolderIdentifier().value());
        bankAccountCommandResponse.setFullName(account.getAccountHolder().getName());
        DepositCommandResponse depositCommandResponse = depositCommandMapper.mapToDepositCommandResponse(account.getDeposit());
        bankAccountCommandResponse.setDeposit(depositCommandResponse);
        return bankAccountCommandResponse;
    }

    public List<BankAccountCommandResponse> mapToBankAccountCommandResponse(List<Account> accounts) {
        return accounts.stream()
                .map(this::mapToBankAccountCommandResponse)
                .collect(Collectors.toList());
    }

    private BankAccountCommand mapToBankAccountCommand(BankAccountCommandRequest request) {
        AccountHolderIdentifier accountHolderIdentifier = new AccountHolderIdentifier(request.getUserId());
        Text lastName = new Text(request.getLastName());
        Text firstName = new Text(request.getFirstName());
        Text email = new Text(request.getEmail());
        AccountHolder accountHolder = AccountHolder.of(accountHolderIdentifier, lastName,
                firstName, email);

        return BankAccountCommand.builder().accountHolder(accountHolder).build();
    }
}
