package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Objects;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.utils.FormatDate.DAY_MONTH_TWO_DIGIT_YEAR_FOUR_DIGIT;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.utils.FormatDate.formatDate;

public record Date(Integer day, Month month, Integer year) implements Comparable<Date> {

    public java.util.Date calendarDate() {

        LocalDate localDate = LocalDate.of(this.year, this.month, this.day);
        return java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public boolean isBefore(Date date) {
        return LocalDate.of(this.year, this.month, this.day).isBefore(LocalDate.of(date.year(), date.month, date.day));
    }

    public boolean isAfter(Date date) {
        return LocalDate.of(this.year, this.month, this.day).isAfter(LocalDate.of(date.year(), date.month, date.day));
    }

    public String formattedDate() {
        LocalDate localDate = LocalDate.of(this.year, this.month, this.day);
        return formatDate(localDate, DAY_MONTH_TWO_DIGIT_YEAR_FOUR_DIGIT );
    }

    @Override
    public int compareTo(Date DateToCompare) {
        if (!Objects.equals(this.year, DateToCompare.year)) {
            return Integer.compare(DateToCompare.year, this.year);
        }
        if (this.month.getValue() != DateToCompare.month.getValue()) {
            return Integer.compare(DateToCompare.month.getValue(), this.month.getValue());
        }
        return Integer.compare(DateToCompare.day, this.day);
    }

    public boolean isBetween(Date startDate, Date endDate) {
        return this.compareTo(startDate) <= 0 && this.compareTo(endDate) >= 0;
    }

    public Date plusDays(int daysToAdd) {
        LocalDate localDate = LocalDate.of(this.year, this.month, this.day).plusDays(daysToAdd);
        return new Date(localDate.getDayOfMonth(), localDate.getMonth(), localDate.getYear());
    }

    public boolean isEqual(LocalDate localDate) {
        return localDate.isEqual(LocalDate.of(this.year, this.month, this.day));
    }
}
