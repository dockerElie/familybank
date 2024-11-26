package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.api;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.mapper.DepositCommandMapper;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.mapper.DepositResponseMapper;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models.DepositCommandRequest;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models.DepositCommandResponse;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models.DepositResponse;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.DepositIdentifier;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.model.DepositRequestUser;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.DepositCommand;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase.AuthenticateUser;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase.ListUsers;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;

@RestController
public class DepositController {

    private final DepositProvider depositProvider;
    private final AccountProvider accountProvider;

    private final AuthenticateUser authenticateUser;
    private final ListUsers listUsers;
    private final ConsultListOfDeposit consultListOfDeposit;
    private final ConfigureUserDepositExpirationDate configureUserDepositExpirationDate;

    private final ValidateDeposit validateDeposit;

    private final DepositCommandMapper depositCommandMapper;
    private final DepositResponseMapper depositResponseMapper;
    private final CancelDeposit cancelDeposit;
    private final RequestDeposit requestDeposit;
    private final ValidateOrRejectDepositRequest validateOrRejectDepositRequest;

    public DepositController(DepositProvider depositProvider, AccountProvider accountProvider, AuthenticateUser authenticateUser,
                             ListUsers listUsers, ConsultListOfDeposit consultListOfDeposit,
                             ConfigureUserDepositExpirationDate configureUserDepositExpirationDate, ValidateDeposit validateDeposit,
                             DepositCommandMapper depositCommandMapper, DepositResponseMapper depositResponseMapper, CancelDeposit cancelDeposit, RequestDeposit requestDeposit, ValidateOrRejectDepositRequest validateOrRejectDepositRequest) {
        this.depositProvider = depositProvider;
        this.accountProvider = accountProvider;
        this.authenticateUser = authenticateUser;
        this.listUsers = listUsers;
        this.consultListOfDeposit = consultListOfDeposit;
        this.configureUserDepositExpirationDate = configureUserDepositExpirationDate;
        this.validateDeposit = validateDeposit;
        this.depositCommandMapper = depositCommandMapper;
        this.depositResponseMapper = depositResponseMapper;
        this.cancelDeposit = cancelDeposit;
        this.requestDeposit = requestDeposit;
        this.validateOrRejectDepositRequest = validateOrRejectDepositRequest;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/api/account/{accountId}/deposit/list.json")
    public ResponseEntity<List<DepositResponse>> consultDeposit(@PathVariable String accountId) {
        User keycloakUser = authenticateUser.login();
        User userFromDB = listUsers.retrieveUserWithRole(keycloakUser.getIdentifier().id(), keycloakUser.getRoles());
        List<DepositResponse> depositResponses = new ArrayList<>();
        if (userFromDB.isActivated()) {
            List<Deposit> depositList = consultListOfDeposit.withDefaultList(AccountId.of(accountId));
            for (Deposit deposit: depositList) {
                depositResponses.add(depositResponseMapper.mapToDepositResponse(deposit));
            }
            return ResponseEntity.ok().body(depositResponses);
        }

        return ResponseEntity.ok().body(Collections.emptyList());
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/api/deposit/{depositIdentifier}/make-deposit.json")
    public ResponseEntity<DepositCommandResponse> makeDeposit(@PathVariable String depositIdentifier, @RequestParam String money) {

        User keycloakUser = authenticateUser.login();
        User userFromDB = listUsers.retrieveUserWithRole(keycloakUser.getIdentifier().id(), keycloakUser.getRoles());
        if (userFromDB.isActivated()) {
            MakeDeposit makeDeposit = new MakeDeposit(money, depositProvider, accountProvider);
            Deposit deposit = makeDeposit.execute(new DepositIdentifier(depositIdentifier));
            return ResponseEntity.ok(depositCommandMapper.mapToDepositCommandResponse(deposit));
        }
        return ResponseEntity.ok().body(new DepositCommandResponse());
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/api/deposit/{depositIdentifier}/validate-deposit.json")
    public ResponseEntity<DepositCommandResponse> validateDeposit(@PathVariable String depositIdentifier) {

        User keycloakUser = authenticateUser.login();
        User userFromDB = listUsers.retrieveUserWithRole(keycloakUser.getIdentifier().id(), keycloakUser.getRoles());

        if (userFromDB.isActivated()) {
            Deposit deposit = validateDeposit.execute(new DepositIdentifier(depositIdentifier));
            return ResponseEntity.ok(depositCommandMapper.mapToDepositCommandResponse(deposit));
        }
        return ResponseEntity.ok().body(new DepositCommandResponse());
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/api/deposit/configure-user-deposit-expiration-date.json")
    public ResponseEntity<Deposit> configureUserDepositExpirationDate(@RequestBody DepositCommandRequest depositCommandRequest) {

        User keycloakUser = authenticateUser.login();
        User userFromDB = listUsers.retrieveUserWithRole(keycloakUser.getIdentifier().id(), keycloakUser.getRoles());

        if (userFromDB.isUser() && userFromDB.isActivated()) {
            DepositCommand depositCommand = depositCommandMapper.mapToDepositCommand(depositCommandRequest);
            return ResponseEntity.ok(configureUserDepositExpirationDate.execute(depositCommand));
        }
        return ResponseEntity.ok(depositBuilder().build());

    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping("/api/deposit/open-request-to-activate-deposit.json")
    public ResponseEntity<DepositCommandResponse> openARequestToActivateADeposit(
            @RequestBody DepositCommandRequest depositCommandRequest) {

        User keycloakUser = authenticateUser.login();
        User userFromDB = listUsers.retrieveUserWithRole(keycloakUser.getIdentifier().id(), keycloakUser.getRoles());

        if (userFromDB.isActivated()) {

            DepositCommand depositCommand = depositCommandMapper.mapToDepositCommand(depositCommandRequest);
            Deposit deposit = requestDeposit.execute(depositCommand);
            return ResponseEntity.ok(depositCommandMapper.mapToDepositCommandResponse(deposit));

        }
        return ResponseEntity.ok().body(new DepositCommandResponse());
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/api/deposit/{depositIdentifier}/cancel-deposit.json")
    public ResponseEntity<DepositCommandResponse> cancelDeposit(@PathVariable String depositIdentifier) {

        User keycloakUser = authenticateUser.login();
        User userFromDB = listUsers.retrieveUserWithRole(keycloakUser.getIdentifier().id(), keycloakUser.getRoles());

        if (userFromDB.isActivated()) {
            Deposit deposit = cancelDeposit.execute(new DepositIdentifier(depositIdentifier));
            return ResponseEntity.ok(depositCommandMapper.mapToDepositCommandResponse(deposit));
        }
        return ResponseEntity.ok().body(new DepositCommandResponse());
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/api/deposit-request-user.json")
    public ResponseEntity<List<DepositRequestUser>> usersMadeDepositRequest() {
        ValidateOrRejectDepositRequest validateOrRejectDepositRequest = new ValidateOrRejectDepositRequest(
                depositProvider, accountProvider);
        User keycloakUser = authenticateUser.login();
        User userFromDB = listUsers.retrieveUserWithRole(keycloakUser.getIdentifier().id(), keycloakUser.getRoles());
        if (userFromDB.isManager() && userFromDB.isActivated()) {
            return ResponseEntity.ok(validateOrRejectDepositRequest.usersMadeDepositRequest());
        }
        return ResponseEntity.ok().body(Collections.emptyList());
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping("/api/validate-deposit-request.json")
    public ResponseEntity<DepositCommandResponse> validateDepositRequest(@RequestBody DepositCommandRequest depositCommandRequest) {

        User keycloakUser = authenticateUser.login();
        User userFromDB = listUsers.retrieveUserWithRole(keycloakUser.getIdentifier().id(), keycloakUser.getRoles());
        if (userFromDB.isActivated()) {
            DepositCommand depositCommand = depositCommandMapper.mapToDepositCommand(depositCommandRequest);
            Deposit deposit = validateOrRejectDepositRequest.accept(depositCommand);
            return ResponseEntity.ok(depositCommandMapper.mapToDepositCommandResponse(deposit));
        }
        return ResponseEntity.ok().body(new DepositCommandResponse());
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping("/api/reject-deposit-request.json")
    public ResponseEntity<DepositCommandResponse> rejectDepositRequest(@RequestBody DepositCommandRequest depositCommandRequest) {

        User keycloakUser = authenticateUser.login();
        User userFromDB = listUsers.retrieveUserWithRole(keycloakUser.getIdentifier().id(), keycloakUser.getRoles());

        if (userFromDB.isActivated()) {
            DepositCommand depositCommand = depositCommandMapper.mapToDepositCommand(depositCommandRequest);
            Deposit deposit = validateOrRejectDepositRequest.reject(depositCommand);
            return ResponseEntity.ok(depositCommandMapper.mapToDepositCommandResponse(deposit));
        }
        return ResponseEntity.ok().body(new DepositCommandResponse());
    }
}
