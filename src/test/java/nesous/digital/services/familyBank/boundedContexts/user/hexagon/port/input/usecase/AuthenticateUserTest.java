package nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase;

import nesous.digital.services.familyBank.boundedContexts.shareKernel.Email;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.UserIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthenticateUserTest {

    private final UserContext userContext = createMock(UserContext.class);
    private AuthenticateUser authenticateUser;

    @BeforeEach
    public void setUp() {
        authenticateUser = new AuthenticateUser(userContext);
    }

    @Test
    public void testLogin_thenReturnedUserDetails() {
        // Arrange
        UserIdentifier userIdentifier = new UserIdentifier("uir-zez-zec");
        Text lastName = new Text("NONO");
        Text firstName = new Text("ELIE");
        Text userName = new Text("noel");
        Email email = Email.of(new Text("elie@com"));
        expect(userContext.getContext()).andReturn(new User(userIdentifier, lastName, firstName, userName,
                email, null, Collections.emptyList()));

        // Act
        replay(userContext);
        User user = authenticateUser.login();

        // Assert
        assertNotNull(user);
        assertEquals(lastName, user.getLastName());
        assertEquals(firstName, user.getFirstName());
        assertEquals(userName, user.getUserName());
        assertEquals(email, user.getEmail());
    }
}
