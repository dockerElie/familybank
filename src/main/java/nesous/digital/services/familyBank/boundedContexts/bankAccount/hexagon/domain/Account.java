package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain;

import lombok.Builder;
import lombok.Getter;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.DepositAccountException;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;

import java.time.LocalDate;
import java.util.UUID;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.ACTIVATED;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationMessages.PREVIOUS_DEPOSIT_STILL_ONGOING;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.utils.FormatDate.DAY_MONTH_TWO_DIGIT_YEAR_FOUR_DIGIT;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.utils.FormatDate.formatDate;


@Getter
@Builder
public class Account {
    private final AccountId accountId;
    private final AccountHolder accountHolder;
    private final Deposit deposit;


    public static AccountId nextAccountId() {
        String account = "A";
        String uuid = UUID.randomUUID().toString();
        LocalDate currentDate = LocalDate.now();
        String currentDateAsString = formatDate(currentDate, DAY_MONTH_TWO_DIGIT_YEAR_FOUR_DIGIT );
        return AccountId.of(account+"-"+uuid.substring(0, 7)+"-"+currentDateAsString);
    }

    public Account updateDepositReminderDate() {
        Deposit deposit = depositBuilder()
                .withIdentifier(this.deposit.getIdentifier())
                .withName(this.deposit.getName())
                .withDescription(this.deposit.getDescription())
                .withDate(this.deposit.getDate())
                .withMoney(this.deposit.getMoney())
                .withExpirationDate(this.deposit.getExpirationDate())
                .withDepositReminderDate(this.deposit.nextDepositReminderDate())
                .withStatus(this.deposit.getStatus())
                .build();
        return new Account(accountId, accountHolder, deposit);
    }

    public Account activateDeposit(Deposit newDeposit) {

        if (this.deposit != null && !this.deposit.isExpired()) {
            throw new DepositAccountException(PREVIOUS_DEPOSIT_STILL_ONGOING);
        }

        Deposit deposit =  depositBuilder()
                .withIdentifier(DepositIdentifier.generate())
                .withName(newDeposit.getName())
                .withDescription(newDeposit.getDescription())
                .withDate(newDeposit.getDate())
                .withExpirationDate(newDeposit.getExpirationDate())
                .withDepositReminderDate(newDeposit.nextDepositReminderDate())
                .withStatus(ACTIVATED)
                .build();
        return new Account(accountId, accountHolder, deposit);
    }

    public Date getDepositReminderDate() {
        return deposit.getDepositReminderDate().date();
    }

    public Date getDepositDate() {
        return this.deposit.getDate();
    }

    public Description getDepositDescription() {
        return this.deposit.getDescription();
    }

    public Status getDepositStatus() {
        return this.deposit.getStatus();
    }

    public ExpirationDate getDepositExpirationDate() {
        return this.deposit.getExpirationDate();
    }

    public UserExpirationDate getDepositUserExpirationDate() {
        return this.deposit.getUserExpirationDate();
    }

    public Text getAccountHolderEmailAddress() {
        return this.accountHolder.email().emailAddress();
    }

    public Text getAccountHolderName() {
        return this.accountHolder.getFullName();
    }

    public DepositIdentifier getDepositIdentifier() {
        return this.deposit.getIdentifier();
    }

    public DepositName getDepositName() {
        return this.deposit.getName();
    }

    public Money getDepositMoney() {

        if (this.deposit.getMoney() == null) {
            return Money.ZERO;
        }
        return this.deposit.getMoney().money();
    }

    public AccountHolderIdentifier getAccountHolderIdentifier() {
        return this.accountHolder.identifier();
    }

    public Text getReason() {
        if (this.deposit.getReason() == null) {
            return new Text("");
        }
        return this.deposit.getReason().text();
    }
}
