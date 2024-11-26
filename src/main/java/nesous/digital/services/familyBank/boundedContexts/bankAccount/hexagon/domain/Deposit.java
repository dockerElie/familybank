package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain;

import lombok.Getter;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.DepositException;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationException;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.utils.DateConversion;
import java.time.LocalDate;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.*;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions.ValidationMessages.*;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.utils.Guard.guard;


@Getter
public class Deposit {

    private final DepositIdentifier identifier;
    private static final int REMINDER_DAYS_PERIOD = 5;
    private final DepositName name;
    private final Description description;
    private final ExpirationDate expirationDate;
    private final Date date;
    private final DepositMoney money;
    private final Status status;
    private final UserExpirationDate userExpirationDate;
    private final DepositReminderDate depositReminderDate;
    private final Reason reason;
    private final AccountId accountId;

    Deposit(DepositName name, Description description, ExpirationDate expirationDate, DepositIdentifier identifier,
            Date date, DepositMoney money, Status status, UserExpirationDate userExpirationDate,
            DepositReminderDate depositReminderDate, Reason reason, AccountId accountId) {
        this.name = name;
        this.description = description;
        this.expirationDate = expirationDate;
        this.identifier = identifier;
        this.date = date;
        this.money = money;
        this.status = status;
        this.userExpirationDate = userExpirationDate;
        this.depositReminderDate = depositReminderDate;
        this.reason = reason;
        this.accountId = accountId;
    }

    public boolean isExpired() {
        LocalDate localDate = LocalDate.now();
        Date today = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
        return this.expirationDate.date().isBefore(today);
    }

    public DepositReminderDate nextDepositReminderDate() {
        if (this.depositReminderDate == null) {
            return new DepositReminderDate(this.date.plusDays(REMINDER_DAYS_PERIOD));
        }

        if (this.userExpirationDate != null) {
            return new DepositReminderDate(userExpirationDate.date());
        }

        return new DepositReminderDate(this.depositReminderDate.date().plusDays(REMINDER_DAYS_PERIOD));
    }

    public Deposit makeDeposit(String money) {
        DepositMoney depositMoney = DepositMoney.of(null);
        try {
            LocalDate localDate = DateConversion.dateToLocalDate(new java.util.Date());
            Date today = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
            if (!money.isEmpty()) {
                double amount = Double.parseDouble(money);
                depositMoney = DepositMoney.of(Money.of(amount));

            }

            if (this.status != ACTIVATED) {
                throw new ValidationException("Please Activate a deposit before adding money");
            }

            if (today.isAfter(this.expirationDate.date())) {
                throw new DepositException("Deposit already expired.");
            }

        } catch (NumberFormatException exception) {
            throw new ValidationException("Wrong value. Please provide a numeric value.");
        }

        return depositBuilder()
                .withAccountId(this.getAccountId())
                .withMoney(depositMoney)
                .withIdentifier(this.identifier)
                .withStatus(DONE)
                .withDate(this.date)
                .withDepositReminderDate(depositMoney.money().equals(Money.ZERO) ? this.depositReminderDate : null)
                .withExpirationDate(this.expirationDate)
                .withUserExpirationDate(this.userExpirationDate)
                .withDescription(this.description)
                .withName(this.name)
                .withReason(getFormattedReason())
                .build();
    }

    public Deposit validate() {

        if (this.status != DONE) {
            throw new ValidationException("Please make a deposit before validating");
        }
        checkOtherExceptions();
        return depositBuilder()
                .withAccountId(this.getAccountId())
                .withMoney(this.money)
                .withIdentifier(this.identifier)
                .withStatus(VALIDATED)
                .withDate(this.date)
                .withExpirationDate(this.expirationDate)
                .withUserExpirationDate(this.userExpirationDate)
                .withDescription(this.description)
                .withName(this.name)
                .withReason(getFormattedReason())
                .build();
    }

    public Deposit configureUserExpirationDate(Date date) {

        if (this.status != DONE) {
            throw new ValidationException("Please make a deposit before configuring user expiration date");
        }
        checkOtherExceptions();
        return depositBuilder()
                .withAccountId(this.getAccountId())
                .withMoney(this.money)
                .withIdentifier(this.identifier)
                .withStatus(this.status)
                .withDate(this.date)
                .withExpirationDate(this.expirationDate)
                .withUserExpirationDate(new UserExpirationDate(date))
                .withDescription(this.description)
                .withName(this.name)
                .withReason(getFormattedReason())
                .build();
    }

    public Deposit openARequest() {

        if (this.status != DONE && this.status != VALIDATED && this.status != CANCELLED
                && this.status != CLOSED) {

            throw new ValidationException("Deposit can be requested only if the status is Validated, Done, Cancelled or Closed");
        }
        return depositBuilder()
                .withIdentifier(this.identifier)
                .withAccountId(this.accountId)
                .withMoney(this.money)
                .withStatus(REQUESTED)
                .withDate(this.date)
                .withExpirationDate(this.expirationDate)
                .withUserExpirationDate(this.userExpirationDate)
                .withDescription(this.description)
                .withName(this.name)
                .withReason(getFormattedReason())
                .build();
    }

    public Deposit cancel() {

        if (this.status != DONE && this.status != VALIDATED) {

            throw new ValidationException("Deposit can be cancelled only if the status is Validated or Done");
        }

        return depositBuilder()
                .withIdentifier(this.identifier)
                .withAccountId(this.accountId)
                .withMoney(DepositMoney.of(null))
                .withStatus(CANCELLED)
                .withDate(this.date)
                .withExpirationDate(this.expirationDate)
                .withUserExpirationDate(null)
                .withDescription(this.description)
                .withName(this.name)
                .withReason(getFormattedReason())
                .build();
    }

    public Deposit accept(ExpirationDate expirationDate) {

        return depositBuilder()
                .withIdentifier(this.identifier)
                .withName(this.name)
                .withDate(this.date)
                .withExpirationDate(expirationDate)
                .withStatus(REACTIVATED)
                .withMoney(DepositMoney.of(null))
                .withUserExpirationDate(null)
                .withReason(getFormattedReason())
                .withAccountId(this.accountId)
                .build();
    }

    public Deposit reject() {

        return depositBuilder()
                .withIdentifier(this.identifier)
                .withName(this.name)
                .withDate(this.date)
                .withExpirationDate(this.expirationDate)
                .withStatus(DENIED)
                .withMoney(this.money)
                .withUserExpirationDate(this.userExpirationDate)
                .withReason(getFormattedReason())
                .withAccountId(this.accountId)
                .build();
    }


    public static class DepositBuilder {
        private DepositName name;
        private Description description;
        private ExpirationDate expirationDate;
        private DepositIdentifier identifier;
        private Date date;
        private DepositMoney money;
        private Status status;
        private UserExpirationDate userExpirationDate;

        private DepositReminderDate depositReminderDate;
        private Reason reason;

        private AccountId accountId;

        public static DepositBuilder depositBuilder() {
            return new DepositBuilder();
        }

        public DepositBuilder withName(DepositName name) {
            guard(name).againstNull(DEPOSIT_NAME_EMPTY);
            this.name = name;
            return this;
        }

        public DepositBuilder withDescription(Description description) {
            this.description = description;
            return this;
        }

        public DepositBuilder withExpirationDate(ExpirationDate expirationDate) {
            guard(date).againstNull(DEPOSIT_DATE_EMPTY);
            guard(expirationDate).againstExpirationDate(date, EXPIRATION_BEFORE_DEPOSIT_DATE);
            this.expirationDate = expirationDate;
            return this;
        }

        public DepositBuilder withIdentifier(DepositIdentifier identifier) {
            this.identifier = identifier;
            return this;
        }

        public DepositBuilder withDate(Date date) {
            this.date = date;
            return this;
        }

        public DepositBuilder withMoney(DepositMoney money) {
            this.money = money;
            return this;
        }

        public DepositBuilder withStatus(Status status) {
            this.status = status;
            return this;
        }

        public DepositBuilder withUserExpirationDate(UserExpirationDate userExpirationDate) {
            guard(userExpirationDate).againstUserExpirationDate(expirationDate, USER_EXPIRATION_BEFORE_EXPIRATION_DATE);
            this.userExpirationDate = userExpirationDate;
            return this;
        }

        public DepositBuilder withDepositReminderDate(DepositReminderDate depositReminderDate) {
            this.depositReminderDate = depositReminderDate;
            return this;
        }

        public DepositBuilder withReason(Reason reason) {
            guard(reason).againstReason(reason, REASON_EMPTY);
            this.reason = reason;
            return this;
        }

        public DepositBuilder withAccountId(AccountId accountId) {
            this.accountId = accountId;
            return this;
        }

        public Deposit build() {
            return new Deposit(name, description, expirationDate, identifier, date, money, status,
                    userExpirationDate, depositReminderDate, reason, accountId);
        }
    }

    private void checkOtherExceptions() {

        LocalDate localDate = DateConversion.dateToLocalDate(new java.util.Date());
        Date today = new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());

        if (today.isAfter(this.expirationDate.date())) {
            throw new DepositException("Deposit already expired.");
        }
    }

    public Reason getFormattedReason() {
        return this.reason == null ? new Reason(new Text("")) : this.reason;
    }

}
