package nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.mappers;

import nesous.digital.services.familyBank.boundedContexts.shareKernel.Email;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.entities.UserEntity;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Status;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.Role;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.UserIdentifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserMapper {

    public UserEntity fromUserToEntity(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setIdentifier(user.getIdentifier().id());
        userEntity.setLastName(user.getLastName().value());
        userEntity.setFirstName(user.getFirstName().value());
        userEntity.setUserName(user.getUserName().value());
        userEntity.setEmail(user.getEmail().emailAddress().value());
        return userEntity;
    }

    public User fromEntityToUser(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        UserIdentifier userIdentifier = new UserIdentifier(userEntity.getIdentifier());
        Text lastName = new Text(userEntity.getLastName());
        Text firstName = new Text(userEntity.getFirstName());
        Text userName = new Text(userEntity.getUserName());
        Email email = Email.of(new Text(userEntity.getEmail()));
        Status status = userEntity.getStatus();
        return new User(userIdentifier, lastName, firstName, userName, email, status, null);
    }

    public User fromEntityToUser(UserEntity userEntity, List<Role> withRoles) {
        if (userEntity == null) {
            return null;
        }
        UserIdentifier userIdentifier = new UserIdentifier(userEntity.getIdentifier());
        Text lastName = new Text(userEntity.getLastName());
        Text firstName = new Text(userEntity.getFirstName());
        Text userName = new Text(userEntity.getUserName());
        Email email = Email.of(new Text(userEntity.getEmail()));
        Status status = userEntity.getStatus();
        return new User(userIdentifier, lastName, firstName, userName, email, status, withRoles);
    }

    public List<User> fromEntityToUser(List<UserEntity> userEntities) {
        if (userEntities.isEmpty()) {
            return Collections.emptyList();
        }

        return userEntities.stream().map(this::fromEntityToUser).toList();
    }
}
