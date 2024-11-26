package nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Email;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.UserIdentifier;


@Data
public class Person {

    @Setter(AccessLevel.NONE)
    private final UserIdentifier identifier;
    @Setter(AccessLevel.NONE)
    private final Text lastName;
    @Setter(AccessLevel.NONE)
    private final Text firstName;
    @Setter(AccessLevel.NONE)
    private final Text userName;
    @Setter(AccessLevel.NONE)
    private final Email email;

    public Person(UserIdentifier identifier, Text lastName, Text firstName, Text userName, Email email) {
        this.identifier = identifier;
        this.lastName = lastName;
        this.firstName = firstName;
        this.userName = userName;
        this.email = email;
    }
}
