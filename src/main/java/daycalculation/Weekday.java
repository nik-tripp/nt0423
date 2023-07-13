package daycalculation;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class Weekday {
    /**
     * Returns the number of weekdays within the given range.
     * @param start LocalDate, inclusive
     * @param end LocalDate, inclusive
     * @return the count of weekdays
     */
    public static long calculateWeekdays(LocalDate start, LocalDate end) {
        // Weekday calculations from https://stackoverflow.com/questions/4600034/calculate-number-of-weekdays-between-two-dates-in-java
        DayOfWeek startW = start.getDayOfWeek();
        DayOfWeek endW = end.getDayOfWeek();
        long days = start.until(end, java.time.temporal.ChronoUnit.DAYS) + 1; // We want to include the end date

        // Remove weekends
        long weekdays = days - (2 * (days / 7));

        // Handle edge days
        if (days % 7 != 0) {
            // If we started or ended on a sunday, there were extra weekend days
            if (startW == DayOfWeek.SUNDAY || endW == DayOfWeek.SUNDAY) {
                weekdays--;

                // If we ended on a sunday and started on a saturday, remove the extra weekend day
                if (startW == DayOfWeek.SATURDAY) {
                    weekdays--;
                }
            }
            // If we started or ended on saturday, remove the extra weekend day
            else if (endW == DayOfWeek.SATURDAY) {
                weekdays--;
            }
            else if (endW.getValue() < startW.getValue() || (startW == DayOfWeek.SATURDAY && endW == DayOfWeek.SUNDAY)) { // If we started later in the week than we ended, there was a whole extra weekend
                weekdays -= 2;
            }
        }

        return weekdays;
    }
}
