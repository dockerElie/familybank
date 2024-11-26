package nesous.digital.services.familyBank.boundedContexts.user.adapter.primary.api;

import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.infra.authentication.SecurityContextHolderAuthenticationProvider;
import nesous.digital.services.familyBank.infra.jwt.JwtAuthConvertor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserContextImplTest {

    private UserContextImpl userContext;

    private final JwtAuthConvertor jwtAuthConvertor = createMock(JwtAuthConvertor.class);

    private final Authentication authentication = createMock(Authentication.class);
    private final SecurityContextHolderAuthenticationProvider securityContextHolderAuthenticationProvider = createMock(SecurityContextHolderAuthenticationProvider.class);

    private final static String CLAIM_TOKEN = "claim_token";

    @BeforeEach
    public void setUp() {
        userContext = new UserContextImpl(jwtAuthConvertor,  securityContextHolderAuthenticationProvider);
    }

    @Test
    @DisplayName("get user context using JWT token")
    public void testGetContext_givenJwtToken_thenReturnUserContext() {

        // Arrange
        GrantedAuthority grantedAuthority = getGrantedAuthority();
        Jwt jwt = mock(Jwt.class);
        expect(securityContextHolderAuthenticationProvider.getAuthentication()).andReturn(authentication);

        expect(jwtAuthConvertor.convert(jwt)).andReturn(Collections.singletonList(grantedAuthority));
        expect(jwt.getClaimAsString(anyString())).andReturn(CLAIM_TOKEN).anyTimes();

        // Act
        replay(securityContextHolderAuthenticationProvider, jwtAuthConvertor);

        User user = userContext.getContext();

        // Assert
        assertNotNull(user);
        assertFalse(user.getRoles().isEmpty());
    }

    private GrantedAuthority getGrantedAuthority() {
        return () -> "manager";
    }
}
