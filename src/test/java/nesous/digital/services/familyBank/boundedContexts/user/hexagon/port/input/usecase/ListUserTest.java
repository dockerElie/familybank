package nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase;

import nesous.digital.services.familyBank.boundedContexts.shareKernel.Email;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Status;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.output.providers.UserProvider;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.Role;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.UserIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class ListUserTest {

    private final UserProvider userProvider = createMock(UserProvider.class);
    private ListUsers listUsers;
    @BeforeEach
    public void setUp() {
        listUsers = new ListUsers(userProvider);
    }

    @Test
    public void testRetrieve_thenReturnRegisteredUsers() {

        // Arrange
        UserIdentifier userIdentifier = new UserIdentifier("uir-zez-zec");
        Text lastName = new Text("NONO");
        Text firstName = new Text("ELIE");
        Text userName = new Text("noel");
        Email email = Email.of(new Text("elie@com"));
        User user = new User(userIdentifier, lastName, firstName, userName, email, Status.ACTIVATE, null);
        List<User> userList = Collections.singletonList(user);
        expect(userProvider.retrieve()).andReturn(userList);

        // Act
        replay(userProvider);
        List<User> users = listUsers.retrieve();

        // Assert
        assertFalse(users.isEmpty());
    }

    @Test
    public void testRetrieveOf_givenIdentifier_thenReturnRegisteredUser() {

        // Arrange
        UserIdentifier userIdentifier = new UserIdentifier("uir-zez-zec");
        Text lastName = new Text("NONO");
        Text firstName = new Text("ELIE");
        Text userName = new Text("noel");
        Email email = Email.of(new Text("elie@com"));
        User user = new User(userIdentifier, lastName, firstName, userName, email, Status.ACTIVATE, null);
        expect(userProvider.of("uir-zez-zec")).andReturn(user);

        // Act
        replay(userProvider);
        User result = listUsers.retrieveOf("uir-zez-zec");

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testRetrieveWithRole_givenIdentifier_thenReturnRegisteredUser() {

        // Arrange
        Role role = new Role("ROLE_USER");
        UserIdentifier userIdentifier = new UserIdentifier("uir-zez-zec");
        Text lastName = new Text("NONO");
        Text firstName = new Text("ELIE");
        Text userName = new Text("noel");
        Email email = Email.of(new Text("elie@com"));
        User user = new User(userIdentifier, lastName, firstName, userName, email, Status.ACTIVATE, Collections.singletonList(role));
        expect(userProvider.of("uir-zez-zec")).andReturn(user);

        // Act
        replay(userProvider);
        User result = listUsers.retrieveOf("uir-zez-zec");

        // Assert
        assertEquals("ROLE_USER", user.getRoles().get(0).role());
        assertNotNull(result);
    }
}
