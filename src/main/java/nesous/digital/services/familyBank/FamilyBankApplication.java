package nesous.digital.services.familyBank;

import nesous.digital.services.familyBank.infra.audit.AuditorAwareImpl;
import nesous.digital.services.familyBank.infra.authentication.SecurityContextHolderAuthenticationProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class FamilyBankApplication {

	private final SecurityContextHolderAuthenticationProvider securityContextHolderAuthenticationProvider;

    public FamilyBankApplication(SecurityContextHolderAuthenticationProvider securityContextHolderAuthenticationProvider) {
        this.securityContextHolderAuthenticationProvider = securityContextHolderAuthenticationProvider;
    }

    public static void main(String[] args) {
		SpringApplication.run(FamilyBankApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public AuditorAware<String> auditorAware() {
		return new AuditorAwareImpl(this.securityContextHolderAuthenticationProvider);
	}
}
