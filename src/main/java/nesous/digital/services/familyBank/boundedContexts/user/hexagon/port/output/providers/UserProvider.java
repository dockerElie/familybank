package nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.output.providers;

import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.entities.UserEntity;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.Role;

import java.util.List;

public interface UserProvider {

    List<User> retrieve();

    User of(String identifier);

    User withRole(String identifier, List<Role> roles);

    UserEntity getEntityOf(String identifier);

    User save(User user);
}
