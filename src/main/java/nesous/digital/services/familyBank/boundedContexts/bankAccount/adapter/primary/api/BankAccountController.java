package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.api;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.mapper.BankAccountCommandMapper;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.mapper.DepositCommandMapper;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models.BankAccountCommandRequest;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models.BankAccountCommandResponse;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models.DepositCommandRequest;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.BankAccountCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.DepositCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit.ActivateDeposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.bankAccount.CreateBankAccount;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BankAccountController {

    private final CreateBankAccount createBankAccount;

    private final ActivateDeposit activateDeposit;
    private final BankAccountCommandMapper accountCommandMapper;
    private final DepositCommandMapper depositCommandMapper;
    public BankAccountController(CreateBankAccount createBankAccount, ActivateDeposit activateDeposit,
                                 BankAccountCommandMapper accountCommandMapper,
                                 DepositCommandMapper depositCommandMapper) {

        this.createBankAccount = createBankAccount;
        this.activateDeposit = activateDeposit;
        this.accountCommandMapper = accountCommandMapper;
        this.depositCommandMapper = depositCommandMapper;
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping("/api/account/deposit/activate-for-all.json")
    public ResponseEntity<List<BankAccountCommandResponse>> activateDepositForAll(
            @RequestParam("accountIds") List<String> accountIds,
            @RequestBody DepositCommandRequest depositCommandRequest) {

        List<BankAccountCommandResponse> bankAccountCommandResponses = new ArrayList<>(accountIds.size());
        for (String accountId: accountIds) {
            BankAccountCommandResponse bankAccountCommandResponse = internalActivateDeposit(accountId, depositCommandRequest);
            bankAccountCommandResponses.add(bankAccountCommandResponse);
        }
        return ResponseEntity.ok().body(bankAccountCommandResponses);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping("/api/account/{accountId}/deposit/activate.json")
    public ResponseEntity<BankAccountCommandResponse> activateDeposit(@PathVariable String accountId,
                                                                      @RequestBody DepositCommandRequest depositCommandRequest) {

        return ResponseEntity.ok().body(internalActivateDeposit(accountId, depositCommandRequest));
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping("/api/account.json")
    public ResponseEntity<List<BankAccountCommandResponse>> createBankAccount(
            @RequestBody List<BankAccountCommandRequest> bankAccountCommandRequestList) {
        List<BankAccountCommand> bankAccountCommandList = accountCommandMapper
                    .mapToBankAccountCommand(bankAccountCommandRequestList);
        List<Account> accounts = createBankAccount.execute(bankAccountCommandList);
        List<BankAccountCommandResponse> bankAccountCommandResponses = accountCommandMapper
                    .mapToBankAccountCommandResponse(accounts);

        return ResponseEntity.ok().body(bankAccountCommandResponses);

    }

    private BankAccountCommandResponse internalActivateDeposit(
            String accountId, DepositCommandRequest depositCommandRequest) {

        DepositCommand depositCommand = this.depositCommandMapper.mapToDepositCommand(depositCommandRequest);
        Account account = activateDeposit.execute(AccountId.of(accountId), depositCommand);
        return accountCommandMapper.mapToBankAccountCommandResponse(account);
    }
}
