package nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.entities;

import lombok.Getter;
import lombok.Setter;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Privilege;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Status;
import nesous.digital.services.familyBank.infra.audit.DatabaseAuditable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = UserEntity.USER_TABLE)
@Getter
@Setter
public class UserEntity extends DatabaseAuditable<String> implements Serializable {

    protected static final String USER_TABLE = "USERS";

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "IDENTIFIER")
    private String identifier;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "EMAIL")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_STATUS")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_PRIVILEGE")
    private Privilege privilege;
}
