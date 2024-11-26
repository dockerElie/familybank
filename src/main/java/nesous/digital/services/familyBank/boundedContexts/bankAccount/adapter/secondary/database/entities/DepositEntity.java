package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.entities;

import lombok.Getter;
import lombok.Setter;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status;
import nesous.digital.services.familyBank.infra.audit.DatabaseAuditable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = DepositEntity.DEPOSIT_TABLE)
@Getter
@Setter
public class DepositEntity extends DatabaseAuditable<String> implements Serializable  {

    protected static final String DEPOSIT_TABLE = "DEPOSITS";
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "DEPOSIT_IDENTIFIER")
    private String depositIdentifier;

    @Column(name = "DEPOSIT_NAME")
    private String name;

    @Column(name = "DEPOSIT_DESCRIPTION")
    private String description;

    @Column(name = "DEPOSIT_MONEY")
    private Double money;

    @Enumerated(EnumType.STRING)
    @Column(name = "DEPOSIT_STATUS")
    private Status status;

    @Column(name = "DEPOSIT_REASON")
    private String reason;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DEPOSIT_DATE")
    private Date date;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DEPOSIT_EXPIRATION_DATE")
    private Date expirationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DEPOSIT_REMINDER_DATE")
    private Date reminderDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DEPOSIT_USER_EXPIRATION_DATE")
    @Getter @Setter
    private Date userExpirationDate;

    @JoinColumn(name = "ACCOUNT_ID")
    @ManyToOne
    private AccountEntity account;

}
