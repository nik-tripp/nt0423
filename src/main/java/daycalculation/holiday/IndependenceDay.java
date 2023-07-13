package daycalculation.holiday;

import java.time.LocalDate;

public class IndependenceDay extends Holiday {
    @Override
    public String getName() {
        return "Independence Day";
    }

    @Override
    public DateRange getObservedDates(int year) {
        LocalDate independenceDay = LocalDate.of(year, 7, 4);

        // If the fourth falls on a weekend, move it to the nearest weekday
        if (independenceDay.getDayOfWeek().getValue() == 6) {
            independenceDay = independenceDay.minusDays(1);
        } else if (independenceDay.getDayOfWeek().getValue() == 7) {
            independenceDay = independenceDay.plusDays(1);
        }

        return new DateRange(independenceDay, independenceDay);
    }
}
