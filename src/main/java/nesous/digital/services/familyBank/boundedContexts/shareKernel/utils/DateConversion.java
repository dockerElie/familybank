package nesous.digital.services.familyBank.boundedContexts.shareKernel.utils;

import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Date;

import java.time.LocalDate;
import java.time.ZoneId;

public class DateConversion {

    public static LocalDate dateToLocalDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    }

    public static Date localDateToNesousDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
    }

    public static java.util.Date localDateToDate(LocalDate localDate) {
        return java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
