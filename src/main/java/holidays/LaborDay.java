package holidays;

import java.time.LocalDate;

public class LaborDay extends Holiday {
    @Override
    public String getName() {
        return "Labor Day";
    }

    @Override
    public DateRange getObservedDates(int year) {
        LocalDate laborDay = LocalDate.of(year, 9, 1);

        // Walk through September until we find the first Monday
        while (laborDay.getDayOfWeek().getValue() != 1) {
            laborDay = laborDay.plusDays(1);
        }

        return new DateRange(laborDay, laborDay);
    }
}
