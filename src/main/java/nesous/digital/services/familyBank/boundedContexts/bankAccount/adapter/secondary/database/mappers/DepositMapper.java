package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.mappers;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.entities.DepositEntity;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.DateConversion.*;

@Component
public class DepositMapper {

    public Deposit fromEntityToDeposit(DepositEntity depositEntity) {

        LocalDate localDate = dateToLocalDate(depositEntity.getDate());
        LocalDate localExpirationDate = dateToLocalDate(depositEntity.getExpirationDate());
        LocalDate localUserExpirationDate = dateToLocalDate(depositEntity.getUserExpirationDate());
        LocalDate localReminderDate = dateToLocalDate(depositEntity.getReminderDate());


        return depositBuilder()
                .withIdentifier(new DepositIdentifier(depositEntity.getDepositIdentifier()))
                .withName(DepositName.of(new Text(depositEntity.getName())))
                .withDescription(new Description(new Text(depositEntity.getDescription())))
                .withDate(localDateToNesousDate(localDate))
                .withExpirationDate(ExpirationDate.of(localDateToNesousDate(localExpirationDate)))
                .withUserExpirationDate(new UserExpirationDate(localDateToNesousDate(localUserExpirationDate)))
                .withMoney(DepositMoney.of(Money.of(depositEntity.getMoney())))
                .withReason(new Reason(new Text(depositEntity.getReason())))
                .withStatus(depositEntity.getStatus())
                .withDepositReminderDate(new DepositReminderDate(localDateToNesousDate(localReminderDate)))
                .withAccountId(new AccountId(depositEntity.getAccount().getAccountId()))
                .build();
    }

    public List<Deposit> fromEntityToDeposit(List<DepositEntity> depositEntities) {

        if (depositEntities.isEmpty()) {
            return Collections.emptyList();
        }
        return depositEntities.stream().map(this::fromEntityToDeposit).toList();
    }

    public DepositEntity fromDepositToEntity(Deposit deposit) {
        DepositEntity depositEntity = new DepositEntity();
        depositEntity.setDepositIdentifier(deposit.getIdentifier().value());
        depositEntity.setMoney(deposit.getMoney().value());
        depositEntity.setDescription(deposit.getDescription().text().value());
        depositEntity.setStatus(deposit.getStatus());
        depositEntity.setName(depositEntity.getName());
        depositEntity.setReason(deposit.getReason().text().value());


        LocalDate localDate = LocalDate.of(deposit.getDate().year(), deposit.getDate().month(), deposit.getDate().day());
        ExpirationDate expirationDate = deposit.getExpirationDate();
        UserExpirationDate userExpirationDate = deposit.getUserExpirationDate();
        DepositReminderDate depositReminderDate = deposit.getDepositReminderDate();
        LocalDate localDateExpirationDate = LocalDate.of(expirationDate.date().year(), expirationDate.date().month(),
                expirationDate.date().day());
        LocalDate localDateUserExpirationDate = LocalDate.of(userExpirationDate.date().year(),
                userExpirationDate.date().month(), userExpirationDate.date().day());
        LocalDate localDateReminderDate = LocalDate.of(depositReminderDate.date().year(), depositReminderDate.date().month(),
                depositReminderDate.date().day());

        depositEntity.setDate(localDateToDate(localDate));
        depositEntity.setExpirationDate(localDateToDate(localDateExpirationDate));
        depositEntity.setUserExpirationDate(localDateToDate(localDateUserExpirationDate));
        depositEntity.setReminderDate(localDateToDate(localDateReminderDate));
        return depositEntity;
    }
}
