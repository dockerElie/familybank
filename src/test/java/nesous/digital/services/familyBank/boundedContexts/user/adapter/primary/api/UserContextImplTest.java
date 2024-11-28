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
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserContextImplTest {

    private UserContextImpl userContext;

    private final JwtAuthConvertor jwtAuthConvertor = createMock(JwtAuthConvertor.class);

    private final JwtAuthenticationToken authentication = createMock(JwtAuthenticationToken.class);
    private final SecurityContextHolderAuthenticationProvider securityContextHolderAuthenticationProvider = createMock(SecurityContextHolderAuthenticationProvider.class);

    @BeforeEach
    public void setUp() {
        userContext = new UserContextImpl(jwtAuthConvertor,  securityContextHolderAuthenticationProvider);
    }

    @Test
    @DisplayName("get user context using JWT token")
    public void testGetContext_givenJwtToken_thenReturnUserContext() {

        // Arrange
        GrantedAuthority grantedAuthority = getGrantedAuthority();
        // Dummy token value
        String tokenValue = "dummyTokenValue123";

        // Issued at and expiration time
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(3600); // 1 hour validity

        // Headers map
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "RS256");
        headers.put("typ", "JWT");

        // Claims map
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("role", "manager");
        claims.put("email", "user@example.com");
        Jwt jwt = new Jwt(tokenValue, issuedAt, expiresAt, headers, claims);
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt, Collections.singletonList(grantedAuthority));

        expect(securityContextHolderAuthenticationProvider.getAuthentication()).andReturn(jwtAuthenticationToken);

        expect(jwtAuthConvertor.convert(jwt)).andReturn(Collections.singletonList(grantedAuthority));

        // Act
        replay(securityContextHolderAuthenticationProvider, jwtAuthConvertor, authentication);

        User user = userContext.getContext();

        // Assert
        assertNotNull(user);
        assertFalse(user.getRoles().isEmpty());
    }

    private GrantedAuthority getGrantedAuthority() {
        return () -> "manager";
    }
}
