package nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.repositories;
import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.entities.UserEntity;
import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.mappers.UserMapper;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Status;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.output.providers.UserProvider;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.Role;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("fakeDBUserProvider")
public class FakeDBUserProvider implements UserProvider {

    private static final String USER_ID = "userId";
    private final UserMapper userMapper;

    private final Map<String, UserEntity> users = new HashMap<>();

    public FakeDBUserProvider(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> retrieve() {
        List<UserEntity> userEntities = (List<UserEntity>) users.values();
        return userMapper.fromEntityToUser(userEntities);
    }

    @Override
    public User of(String identifier) {
        UserEntity userEntity = users.get(USER_ID);
        return userMapper.fromEntityToUser(userEntity);
    }

    @Override
    public User withRole(String identifier, List<Role> roles) {
        UserEntity userEntity = users.get(USER_ID);
        return userMapper.fromEntityToUser(userEntity, roles);
    }

    @Override
    public UserEntity getEntityOf(String identifier) {
        return null;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userMapper.fromUserToEntity(user);
        userEntity.setStatus(Status.ACTIVATE);
        users.put(USER_ID, userEntity);
        return user;
    }
}
