package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.mapper;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models.DepositCommandRequest;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.primary.models.DepositCommandResponse;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.command.DepositCommand;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import org.springframework.stereotype.Component;


import static nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.DateConversion.dateToLocalDate;
import static nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.DateConversion.localDateToNesousDate;

@Component
public class DepositCommandMapper {

    public DepositCommandResponse mapToDepositCommandResponse(Deposit deposit) {
        DepositCommandResponse depositCommandResponse = new DepositCommandResponse();
        depositCommandResponse.setIdentifier(deposit.getIdentifier().value());
        depositCommandResponse.setAccountId(deposit.getAccountId().accountId());
        depositCommandResponse.setDate(deposit.getDate().calendarDate());
        depositCommandResponse.setExpirationDate(deposit.getExpirationDate().date().calendarDate());
        depositCommandResponse.setUserExpirationDate(deposit.getUserExpirationDate().date().calendarDate());
        depositCommandResponse.setMoney(deposit.getMoney().value());
        depositCommandResponse.setDepositReminderDate(deposit.getDepositReminderDate().date().calendarDate());
        depositCommandResponse.setDescription(deposit.getDescription().value());
        depositCommandResponse.setName(deposit.getName().value());
        depositCommandResponse.setReason(deposit.getReason().value());
        depositCommandResponse.setStatus(deposit.getStatus().name());
        return depositCommandResponse;
    }
    public DepositCommand mapToDepositCommand(DepositCommandRequest request) {
        Date depositDate = localDateToNesousDate(dateToLocalDate(request.getDate()));
        Date expirationDate = localDateToNesousDate(dateToLocalDate(request.getExpirationDate()));
        DepositCommand depositCommand = DepositCommand.builder()
                .depositName(DepositName.of(new Text(request.getName())))
                .description(new Description(new Text(request.getDescription())))
                .date(depositDate)
                .expirationDate(ExpirationDate.of(expirationDate))
                .build();


        if (request.getIdentifier() != null) {
            DepositIdentifier depositIdentifier = new DepositIdentifier(request.getIdentifier());
            Date userExpirationDate = localDateToNesousDate(dateToLocalDate(request.getUserExpirationDate()));
            depositCommand = DepositCommand.builder()
                    .depositIdentifier(depositIdentifier)
                    .depositName(DepositName.of(new Text(request.getName())))
                    .description(new Description(new Text(request.getDescription())))
                    .date(depositDate)
                    .expirationDate(ExpirationDate.of(expirationDate))
                    .userExpirationDate(new UserExpirationDate(userExpirationDate))
                    .reason(new Reason(new Text(request.getReason())))
                    .build();

        }

        return depositCommand;
    }
}
