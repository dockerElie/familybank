package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.repositories;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.entities.DepositEntity;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.mappers.DepositMapper;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Account;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.ACTIVATED;
import static org.easymock.EasyMock.*;

public class DBDepositProviderTest {

    private DBDepositProvider dbDepositProvider;

    DepositRepository depositRepository = createMock(DepositRepository.class);
    DepositMapper depositMapper = createMock(DepositMapper.class);

    @BeforeEach
    public void setup() {
        dbDepositProvider = new DBDepositProvider(depositRepository, depositMapper);
    }

    @Test
    public void searchDepositByMaximumUserExpirationDate_givenListOfDeposit_thenReturnDepositWithMaximumUserExpirationDate() {

        // Arrange
        UserExpirationDate maxUserExpirationDate = new UserExpirationDate(new Date(1, Month.JANUARY, 2024).plusDays(10));
        Date depositDate = new Date(3, Month.JANUARY, 2023);
        DepositReminderDate depositReminderDate = new DepositReminderDate(depositDate.plusDays(5));
        AccountId accountId = Account.nextAccountId();

        java.util.Date calendarDate = new java.util.Date(System.currentTimeMillis() + 10000000);
        java.util.Date calendarDate1 = new java.util.Date(System.currentTimeMillis() + 20000000);

        DepositEntity depositEntity = new DepositEntity();
        depositEntity.setUserExpirationDate(calendarDate);

        DepositEntity depositEntity1 = new DepositEntity();
        depositEntity1.setUserExpirationDate(calendarDate1);

        List<DepositEntity> depositEntities = new ArrayList<>();
        depositEntities.add(depositEntity1);
        depositEntities.add(depositEntity);

        Deposit deposit = depositBuilder()
                .withIdentifier(DepositIdentifier.generate())
                .withName(DepositName.of(new Text("deposit name")))
                .withDate(depositDate)
                .withExpirationDate(ExpirationDate.of(new Date(1, Month.JANUARY, 2024)))
                .withUserExpirationDate(new UserExpirationDate(new Date(1, Month.JANUARY, 2024)))
                .withDepositReminderDate(depositReminderDate)
                .withDescription(new Description(new Text("deposit name")))
                .withMoney(DepositMoney.of(Money.of(100)))
                .withStatus(ACTIVATED)
                .withAccountId(accountId)
                .build();

        Deposit deposit1 = depositBuilder()
                .withIdentifier(DepositIdentifier.generate())
                .withName(DepositName.of(new Text("deposit name1")))
                .withDate(depositDate)
                .withExpirationDate(ExpirationDate.of(new Date(3, Month.JANUARY, 2024)))
                .withUserExpirationDate(maxUserExpirationDate)
                .withDepositReminderDate(depositReminderDate)
                .withDescription(new Description(new Text("deposit name1")))
                .withMoney(DepositMoney.of(Money.of(200)))
                .withStatus(ACTIVATED)
                .withAccountId(accountId)
                .build();

        List<Deposit> deposits = new ArrayList<>();
        deposits.add(deposit1);
        deposits.add(deposit);

        expect(depositRepository.findByAccountAccountIdOrderByUserExpirationDateDesc(accountId.accountId()))
                .andReturn(depositEntities);

        expect(depositMapper.fromEntityToDeposit(isA(List.class))).andReturn(deposits);

        // Act
        replay(depositRepository, depositMapper);
        Deposit result = dbDepositProvider.searchDepositByMaximumUserExpirationDate(accountId);

        // Assert
        Assertions.assertEquals(maxUserExpirationDate.date(), result.getUserExpirationDate().date());
    }
}
