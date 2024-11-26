package nesous.digital.services.familyBank.boundedContexts.shareKernel.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateTimeComponent {

    public LocalDate now() {
        return LocalDate.now();
    }
}
