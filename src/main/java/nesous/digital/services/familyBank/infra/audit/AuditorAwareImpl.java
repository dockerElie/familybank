package nesous.digital.services.familyBank.infra.audit;

import nesous.digital.services.familyBank.infra.authentication.SecurityContextHolderAuthenticationProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    private final SecurityContextHolderAuthenticationProvider securityContextHolderAuthenticationProvider;

    public AuditorAwareImpl(SecurityContextHolderAuthenticationProvider securityContextHolderAuthenticationProvider) {
        this.securityContextHolderAuthenticationProvider = securityContextHolderAuthenticationProvider;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = securityContextHolderAuthenticationProvider.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();

        return Optional.ofNullable(oidcUser.getPreferredUsername());
    }
}
