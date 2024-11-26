package nesous.digital.services.familyBank.boundedContexts.user.adapter.primary.api;

import nesous.digital.services.familyBank.boundedContexts.shareKernel.Email;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase.UserContext;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.Role;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.UserIdentifier;
import nesous.digital.services.familyBank.infra.authentication.SecurityContextHolderAuthenticationProvider;
import nesous.digital.services.familyBank.infra.jwt.JwtAuthConvertor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component("userContextImpl")
public class UserContextImpl implements UserContext {

    private final static String JWT_FAMILY_NAME_CLAIMS = "family_name";
    private final static String JWT_GIVEN_NAME_CLAIMS = "given_name";

    private final static String JWT_PREFERRED_USER_NAME_CLAIMS = "preferred_username";

    private final static String JWT_EMAIL_CLAIMS = "email";

    private final JwtAuthConvertor jwtAuthConvertor;

    private final SecurityContextHolderAuthenticationProvider securityContextHolderAuthenticationProvider;

    public UserContextImpl(JwtAuthConvertor jwtAuthConvertor,
                           SecurityContextHolderAuthenticationProvider securityContextHolderAuthenticationProvider) {
        this.jwtAuthConvertor = jwtAuthConvertor;
        this.securityContextHolderAuthenticationProvider = securityContextHolderAuthenticationProvider;
    }

    @Override
    public User getContext() {
        Authentication authentication = securityContextHolderAuthenticationProvider.getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthToken)) {
            throw new IllegalStateException("Authentication object is not a valid JwtAuthenticationToken");
        }

        Jwt jwt = jwtAuthToken.getToken(); // Retrieve the JWT token from the authentication object

        return mapToUser(jwt);
    }

    private User mapToUser(Jwt jwt) {

        UserIdentifier userId = new UserIdentifier(jwt.getSubject());
        Text lastName = new Text(jwt.getClaimAsString(JWT_FAMILY_NAME_CLAIMS));
        Text firstName = new Text(jwt.getClaimAsString(JWT_GIVEN_NAME_CLAIMS));
        Text username = new Text(jwt.getClaimAsString(JWT_PREFERRED_USER_NAME_CLAIMS));
        Email email = new Email(new Text(jwt.getClaimAsString(JWT_EMAIL_CLAIMS)));

        Collection<GrantedAuthority> grantedAuthorities = jwtAuthConvertor.convert(jwt);
        assert grantedAuthorities != null;
        List<Role> roles = grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
                .stream()
                .map(Role::new)
                .toList();
        return new User(userId, lastName, firstName, username, email, null, roles);
    }
}
