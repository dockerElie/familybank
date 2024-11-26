package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormatDate {

    private FormatDate() {}

    public static final String DAY_MONTH_TWO_DIGIT_YEAR_FOUR_DIGIT = "dd-MM-yyyy";

    public static String formatDate(LocalDate date, String dateFormat) {

        // Create a DateTimeFormatter with the desired pattern and locale
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat, Locale.FRENCH);

        // Format the LocalDate using the formatter
        return date.format(formatter);
    }
}
