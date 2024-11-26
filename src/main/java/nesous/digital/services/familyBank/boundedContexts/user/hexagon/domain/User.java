package nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain;

import lombok.Getter;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Email;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Status;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.Role;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.UserIdentifier;

import java.util.List;

@Getter
public class User extends Person {

    private final List<Role> roles;
    private final Status status;

    public User(UserIdentifier identifier, Text lastName, Text firstName, Text userName, Email email,
                Status status, List<Role> roles) {
        super(identifier, lastName, firstName, userName, email);
        this.status = status;
        this.roles = roles;
    }

    public boolean isManager() {
        Role managerRole = new Role("ROLE_MANAGER".toLowerCase());
        return this.roles != null && this.roles.contains(managerRole);
    }

    public boolean isUser() {
        Role managerRole = new Role("ROLE_USER".toLowerCase());
        return this.roles != null && this.roles.contains(managerRole);
    }

    public boolean isActivated() {
        return this.status != null && this.status.equals(Status.ACTIVATE);
    }
}
