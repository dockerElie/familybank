package nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase;

import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.output.providers.UserProvider;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.Role;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.UserIdentifier;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ActivateUserTest {
    private static final String USER_ID = "userId";
    private static final String LAST_NAME = "lastName";
    private static final String FIRST_NAME = "firstName";
    private static final String USER_NAME = "userName";

    private final UserProvider userProvider = EasyMock.createMock(UserProvider.class);


    private ActivateUser activateUser;

    @BeforeEach
    public void setUp() {
        activateUser = new ActivateUser(userProvider);
    }

    @Test
    public void register_givenUser_thenReturnRegisteredUser() {

        // Arrange
        UserIdentifier userIdentifier = new UserIdentifier(USER_ID);
        Text lastName = new Text(LAST_NAME);
        Text firstName = new Text(FIRST_NAME);
        Text userName = new Text(USER_NAME);
        Role role = new Role("MANAGER");

        User user = new User(userIdentifier, lastName, firstName, userName, null, null,
                Collections.singletonList(role));

        expect(userProvider.save(user)).andReturn(user);

        // Act
        replay(userProvider);
        User result = activateUser.register(user);

        // Assert
        assertNotNull(result);
    }
}
