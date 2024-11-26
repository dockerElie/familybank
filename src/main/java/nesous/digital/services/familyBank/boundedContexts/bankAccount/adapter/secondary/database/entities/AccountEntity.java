package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.database.entities;

import lombok.Getter;
import lombok.Setter;
import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.entities.UserEntity;
import nesous.digital.services.familyBank.infra.audit.DatabaseAuditable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = AccountEntity.ACCOUNT_TABLE)
@Getter
@Setter
public class AccountEntity extends DatabaseAuditable<String> implements Serializable {

    protected static final String ACCOUNT_TABLE = "ACCOUNTS";

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "ACCOUNT_ID")
    private String accountId;

    @OneToOne
    @JoinColumn(name = "user_identifier", nullable = false)
    private UserEntity userId;

    @OneToMany(mappedBy="account", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<DepositEntity> deposits;
}
