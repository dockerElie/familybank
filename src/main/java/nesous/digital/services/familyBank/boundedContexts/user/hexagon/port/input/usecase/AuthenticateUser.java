package nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase;

import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;

public class AuthenticateUser {

    private final UserContext userContext;

    public AuthenticateUser(UserContext userContext) {
        this.userContext = userContext;
    }

    public User login() {
        return userContext.getContext();
    }
}
