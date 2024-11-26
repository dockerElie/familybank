package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.mapper;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models.DepositResponse;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import org.springframework.stereotype.Component;

@Component
public class DepositResponseMapper {

    public DepositResponse mapToDepositResponse(Deposit deposit) {

        DepositResponse depositResponse = new DepositResponse();
        depositResponse.setIdentifier(deposit.getIdentifier().value());
        depositResponse.setDescription(deposit.getDescription().value());
        depositResponse.setName(deposit.getName().value());
        depositResponse.setDate(deposit.getDate().calendarDate());
        depositResponse.setExpirationDate(deposit.getExpirationDate().date().calendarDate());
        depositResponse.setUserExpirationDate(deposit.getUserExpirationDate().date().calendarDate());
        depositResponse.setAmount(deposit.getMoney().value());
        depositResponse.setStatus(deposit.getStatus().toString());

        return depositResponse;
    }
}
