package nesous.digital.services.familyBank.boundedContexts.user.adapter.primary.mappers;

import nesous.digital.services.familyBank.boundedContexts.user.adapter.primary.models.UserPresenter;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.Role;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserPresenterMapper {

    public UserPresenter mapToUserPresenter(User user) {

        List<String> roles = user.getRoles().stream().map(Role::role).toList();
        return new UserPresenter(user.getIdentifier().id(), user.getLastName().value(), user.getFirstName().value(),
                user.getUserName().value(), user.getEmail().emailAddress().value(), roles, user.getStatus().name());
    }

    public List<UserPresenter> mapToUserPresenter(List<User> users) {

        return users.stream()
                .map(this::mapToUserPresenter)
                .toList();
    }

}
