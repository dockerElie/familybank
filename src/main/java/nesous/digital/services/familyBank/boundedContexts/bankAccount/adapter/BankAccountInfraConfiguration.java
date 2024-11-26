package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.logging.LoggingProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.bankAccount.CreateBankAccount;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.AccountProvider;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase.ActivateUser;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase.AuthenticateUser;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase.ListUsers;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase.UserContext;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.output.providers.UserProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

@Configuration
@PropertySources({
        @PropertySource("classpath:application.properties")
})
public class BankAccountInfraConfiguration implements WebMvcConfigurer {

    private final Environment environment;

    private final AccountProvider accountProvider;

    private final DepositProvider depositProvider;

    private final LoggingProvider loggingProvider;
    private final UserProvider userProvider;
    private final UserContext userContext;

    public BankAccountInfraConfiguration(
            Environment environment, AccountProvider accountProvider, DepositProvider depositProvider,
            LoggingProvider loggingProvider,
            @Qualifier("dbUserProvider") UserProvider userProvider,
            @Qualifier("userContextImpl") UserContext userContext) {

        this.environment = environment;
        this.accountProvider = accountProvider;
        this.depositProvider = depositProvider;
        this.loggingProvider = loggingProvider;
        this.userProvider = userProvider;
        this.userContext = userContext;
    }

    // Configure ResourceBundle Message
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    // Configure JavaMailSender
    @Bean
    public JavaMailSender mailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(environment.getProperty("spring.mail.host"));
        mailSender.setPort(Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.mail.port"))));
        mailSender.setUsername(environment.getProperty("spring.mail.username"));
        mailSender.setPassword(environment.getProperty("spring.mail.password"));

        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.starttls.enable", "true");
        mailProperties.put("mail.smtp.auth", "true");
        mailProperties.put("mail.transport.protocol", "smtp");
        mailProperties.put("mail.debug", "true");

        mailSender.setJavaMailProperties(mailProperties);

        return mailSender;
    }

    /**
     * In order for our application to be able to determine which locale is currently in use,
     * we need to add a LocaleResolver bean:
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.FRENCH); // Default locale
        return slr;
    }

    /**
     * Interceptor bean that will switch to a new locale based on
     * the value of the lang parameter when present on the request:
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();

        resolver.setPrefix("templates/"); // Location of thymeleaf template
        resolver.setCacheable(false); // Turning of cache to facilitate template changes
        resolver.setSuffix(".html"); // Template file extension
        resolver.setTemplateMode("HTML"); // Template Type
        resolver.setCharacterEncoding("UTF-8");

        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        engine.setMessageSource(messageSource());

        return engine;
    }

    @Bean
    public CreateBankAccount createBankAccount() {
        return new CreateBankAccount(accountProvider, loggingProvider);
    }

    @Bean
    public AuthenticateUser authenticateUser() {
        return new AuthenticateUser(userContext);
    }

    @Bean
    public ActivateDeposit activateDeposit() {
        return new ActivateDeposit(accountProvider, loggingProvider);
    }

    @Bean
    public ListUsers listUsers() {
        return new ListUsers(userProvider);
    }

    @Bean
    public ActivateUser activateUser() {
        return new ActivateUser(userProvider);
    }

    @Bean
    public ConsultListOfDeposit consultListOfDeposit() {
        return new ConsultListOfDeposit(depositProvider);
    }

    @Bean
    public ConfigureUserDepositExpirationDate configureUserDepositExpirationDate() {
        return new ConfigureUserDepositExpirationDate(depositProvider, accountProvider);
    }

    @Bean
    public ValidateDeposit validateDeposit() {
        return new ValidateDeposit(depositProvider, accountProvider);
    }

    @Bean
    public CancelDeposit cancelDeposit() {
        return new CancelDeposit(depositProvider, accountProvider);
    }

    @Bean
    public RequestDeposit requestDeposit() {
        return new RequestDeposit(depositProvider, accountProvider);
    }

    @Bean
    public ValidateOrRejectDepositRequest validateOrRejectDepositRequest() {
        return new ValidateOrRejectDepositRequest(depositProvider, accountProvider);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
