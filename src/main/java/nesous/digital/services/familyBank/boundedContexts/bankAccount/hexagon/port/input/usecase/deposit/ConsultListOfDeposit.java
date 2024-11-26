package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;

import lombok.RequiredArgsConstructor;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.AccountId;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.model.DepositFilter;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.DepositException;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;

import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ConsultListOfDeposit {

    private final DepositProvider depositProvider;

    public List<Deposit> withDefaultList(AccountId accountId) {

        List<Deposit> listOfDeposit = depositProvider.of(accountId);

        List<Deposit> result = new ArrayList<>(listOfDeposit.stream().filter(deposit ->
                Objects.equals(deposit.getDate().year(), Year.now().getValue())).toList());
        result.sort(Comparator.comparing(Deposit::getDate));

        return result;
    }

    public List<Deposit> searchDepositInSpecificStatuses(List<Status> statuses, AccountId accountId) {

        List<Deposit> listOfDeposit = depositProvider.of(accountId);

        List<Deposit> result = new ArrayList<>(listOfDeposit.stream().filter(deposit ->
                statuses.contains(deposit.getStatus())).toList());
        result.sort(Comparator.comparing(Deposit::getDate));

        return result;
    }

    public List<Deposit> searchDepositIdentifierContainingSpecificCharacter(DepositFilter filter,
                                                                            AccountId accountId) {
        List<Deposit> listOfDeposit = depositProvider.of(accountId);

        List<Deposit> result = new ArrayList<>(listOfDeposit.stream().filter(deposit -> deposit.getIdentifier().value().toLowerCase()
                .contains(filter.getSearchByIdentifier().toLowerCase())).toList());
        result.sort(Comparator.comparing(Deposit::getDate));

        return result;
    }

    public List<Deposit> searchDepositWithSpecificStatusBetweenDates(DepositFilter filter,
                                                                     AccountId accountId) {

        if (filter.getSearchByStatus() == null) {
            throw new DepositException("status should not be null");
        }

        if (filter.getSearchByStartDate() == null) {
            throw new DepositException("start date should not be null");
        }

        if (filter.getSearchByEndDate() == null) {
            throw new DepositException("start date should not be null");
        }
        List<Deposit> listOfDeposit = depositProvider.of(accountId);

        List<Deposit> result = new ArrayList<>(
                listOfDeposit
                        .stream().filter(deposit -> deposit.getDate().isBetween(filter.getSearchByStartDate(), filter.getSearchByEndDate())).toList()
                        .stream().filter(deposit -> deposit.getStatus().equals(filter.getSearchByStatus())).toList());
        result.sort(Comparator.comparing(Deposit::getDate));
        return result;
    }

    public List<Deposit> of(AccountId accountId) {
        return depositProvider.of(accountId);
    }
}
