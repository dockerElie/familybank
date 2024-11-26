package nesous.digital.services.familyBank.infra.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthConvertor implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";

    private static final String ROLES_PREFIX = "ROLE_";
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS_CLAIM);
        Collection<String> roles;
        if (realmAccess == null) {
            return Set.of();
        }
        roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
        return roles.stream().map(role  -> new SimpleGrantedAuthority(ROLES_PREFIX + role))
                .collect(Collectors.toSet());
    }
}
