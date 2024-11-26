package nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.repositories;

import lombok.RequiredArgsConstructor;
import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.entities.UserEntity;
import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.mappers.UserMapper;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Privilege;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Status;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.output.providers.UserProvider;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.Role;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("dbUserProvider")
@RequiredArgsConstructor
public class DBUserProvider implements UserProvider {

    private final UserRepository repository;
    private final UserMapper userMapper;
    @Override
    public List<User> retrieve() {
        List<UserEntity> userEntities = repository.findAll();
        return userMapper.fromEntityToUser(userEntities);
    }

    @Override
    public User of(String identifier) {
        UserEntity userEntity = repository.findByIdentifier(identifier);
        return userMapper.fromEntityToUser(userEntity);
    }

    @Override
    public User withRole(String identifier, List<Role> roles) {
        UserEntity userEntity = repository.findByIdentifier(identifier);
        return userMapper.fromEntityToUser(userEntity, roles);
    }

    @Override
    public UserEntity getEntityOf(String identifier) {
        return repository.findByIdentifier(identifier);
    }

    @Override
    @Transactional
    public User save(User user) {
        UserEntity userEntity = userMapper.fromUserToEntity(user);
        userEntity.setStatus(Status.ACTIVATE);
        userEntity.setPrivilege(user.isManager() ? Privilege.MANAGER : Privilege.USER);
        repository.save(userEntity);
        return user;
    }
}
