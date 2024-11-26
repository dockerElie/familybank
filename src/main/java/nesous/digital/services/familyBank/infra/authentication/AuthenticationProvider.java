package nesous.digital.services.familyBank.infra.authentication;

import org.springframework.security.core.Authentication;

public interface AuthenticationProvider {

    Authentication getAuthentication();
}
