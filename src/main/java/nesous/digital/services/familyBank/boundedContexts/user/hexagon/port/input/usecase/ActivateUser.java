package nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase;

import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.output.providers.UserProvider;

public class ActivateUser {

    private final UserProvider userProvider;
    public ActivateUser(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    public User register(User user) {
        return userProvider.save(user);
    }
}
