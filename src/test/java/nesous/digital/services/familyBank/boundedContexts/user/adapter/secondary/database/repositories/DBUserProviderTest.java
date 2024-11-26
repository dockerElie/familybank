package nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.repositories;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.entities.UserEntity;
import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.mappers.UserMapper;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Status;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.Role;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.UserIdentifier;
import org.easymock.Capture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class DBUserProviderTest {

    private static final String USER_ID = "userId";
    private static final String LAST_NAME = "lastName";
    private static final String FIRST_NAME = "firstName";
    private static final String USER_NAME = "userName";
    UserRepository userRepository = createMock(UserRepository.class);
    UserMapper userMapper = createMock(UserMapper.class);

    private DBUserProvider dbUserProvider;

    @BeforeEach
    public void setup() {
        dbUserProvider = new DBUserProvider(userRepository, userMapper);
    }

    @Test
    public void testRetrieve_thenReturnListOfUser() {

        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setIdentifier(USER_ID);
        userEntity.setLastName(LAST_NAME);
        userEntity.setFirstName(FIRST_NAME);
        userEntity.setUserName(USER_NAME);
        userEntity.setStatus(Status.ACTIVATE);

        UserIdentifier userIdentifier = new UserIdentifier(userEntity.getIdentifier());
        Text lastName = new Text(userEntity.getLastName());
        Text firstName = new Text(userEntity.getFirstName());
        Text userName = new Text(userEntity.getUserName());
        Status status = userEntity.getStatus();

        User user = new User(userIdentifier, lastName, firstName, userName, null, status, null);

        expect(userRepository.findAll()).andReturn(Collections.singletonList(userEntity));
        expect(userMapper.fromEntityToUser(isA(List.class))).andReturn(Collections.singletonList(user));

        // Act
        replay(userRepository, userMapper);
        List<User> result = dbUserProvider.retrieve();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(USER_ID, result.get(0).getIdentifier().id());
        verify(userRepository, userMapper);
    }

    @Test
    public void testRetrieveById_givenIdentifier_thenReturnUser() {

        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setIdentifier(USER_ID);
        userEntity.setLastName(LAST_NAME);
        userEntity.setFirstName(FIRST_NAME);
        userEntity.setUserName(USER_NAME);
        userEntity.setStatus(Status.ACTIVATE);

        UserIdentifier userIdentifier = new UserIdentifier(userEntity.getIdentifier());
        Text lastName = new Text(userEntity.getLastName());
        Text firstName = new Text(userEntity.getFirstName());
        Text userName = new Text(userEntity.getUserName());
        Status status = userEntity.getStatus();

        User user = new User(userIdentifier, lastName, firstName, userName, null, status, null);

        expect(userRepository.findByIdentifier(USER_ID)).andReturn(userEntity);
        expect(userMapper.fromEntityToUser(isA(UserEntity.class))).andReturn(user);

        // Act
        replay(userRepository, userMapper);
        User result = dbUserProvider.of(USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(USER_ID, result.getIdentifier().id());
        verify(userRepository, userMapper);
    }

    @Test
    public void save_givenUser_thenSave() {

        // Arrange
        Capture<UserEntity> userEntityCapture = newCapture();
        UserIdentifier userIdentifier = new UserIdentifier(USER_ID);
        Text lastName = new Text(LAST_NAME);
        Text firstName = new Text(FIRST_NAME);
        Text userName = new Text(USER_NAME);
        Role role = new Role("MANAGER");

        User user = new User(userIdentifier, lastName, firstName, userName, null, null,
                Collections.singletonList(role));
        UserEntity userEntity = new UserEntity();
        userEntity.setIdentifier(USER_ID);
        userEntity.setLastName(LAST_NAME);
        userEntity.setFirstName(FIRST_NAME);
        userEntity.setUserName(USER_NAME);

        expect(userMapper.fromUserToEntity(isA(User.class))).andReturn(userEntity);
        expect(userRepository.save(capture(userEntityCapture))).andReturn(userEntity);

        // Act
        replay(userRepository, userMapper);
        User result = dbUserProvider.save(user);

        // Assert
        UserEntity captureResult = userEntityCapture.getValue();
        assertNotNull(result);
        assertEquals(USER_ID, result.getIdentifier().id());
        assertEquals(Status.ACTIVATE, captureResult.getStatus());
        verify(userRepository, userMapper);
    }
}
