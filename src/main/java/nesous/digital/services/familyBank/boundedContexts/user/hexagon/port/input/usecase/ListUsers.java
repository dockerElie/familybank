package nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase;

import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.output.providers.UserProvider;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.Role;

import java.util.List;

public class ListUsers {

    private final UserProvider userProvider;

    public ListUsers(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    public List<User> retrieve() {
        return userProvider.retrieve();
    }

    public User retrieveOf(String identifier) { return userProvider.of(identifier); }

    public User retrieveUserWithRole(String identifier, List<Role> roles) { return userProvider.withRole(identifier, roles); }
}
