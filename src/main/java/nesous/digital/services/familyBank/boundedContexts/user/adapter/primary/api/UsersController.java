package nesous.digital.services.familyBank.boundedContexts.user.adapter.primary.api;

import nesous.digital.services.familyBank.boundedContexts.user.adapter.primary.mappers.UserPresenterMapper;
import nesous.digital.services.familyBank.boundedContexts.user.adapter.primary.models.UserPresenter;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase.ActivateUser;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase.AuthenticateUser;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase.ListUsers;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class UsersController {

    private final ListUsers listUsers;
    private final AuthenticateUser authenticateUser;
    private final ActivateUser activateUser;

    private final UserPresenterMapper userPresenterMapper;

    public UsersController(ListUsers listUsers, AuthenticateUser authenticateUser, ActivateUser activateUser, UserPresenterMapper userPresenterMapper) {

        this.listUsers = listUsers;
        this.authenticateUser = authenticateUser;
        this.activateUser = activateUser;
        this.userPresenterMapper = userPresenterMapper;
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/api/users.json")
    public ResponseEntity<List<UserPresenter>> listUsers() {
        User keycloakUser = authenticateUser.login();
        User userFromDB = listUsers.retrieveUserWithRole(keycloakUser.getIdentifier().id(), keycloakUser.getRoles());
        if (userFromDB.isActivated()) {
            return ResponseEntity.ok().body(userPresenterMapper.mapToUserPresenter(listUsers.retrieve()));
        }
        return ResponseEntity.ok().body(Collections.emptyList());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_USER')")
    @GetMapping("/api/user-profile.json")
    public ResponseEntity<UserPresenter> userProfile() {
        User keycloakUser = authenticateUser.login();
        UserPresenter userPresenter;
        User userFromDB = listUsers.retrieveUserWithRole(keycloakUser.getIdentifier().id(), keycloakUser.getRoles());
        if (userFromDB == null) {
            // Activated user
            User user = activateUser.register(keycloakUser);
            userPresenter = userPresenterMapper.mapToUserPresenter(user);
            return ResponseEntity.ok().body(userPresenter);
        }
        userPresenter = userFromDB.isActivated() ? userPresenterMapper.mapToUserPresenter(userFromDB) : new UserPresenter(
                null, null, null, null, null, null, null);
        return ResponseEntity.ok().body(userPresenter);
    }
}
