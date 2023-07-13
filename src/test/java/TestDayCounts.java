import daycalculation.Weekday;
import daycalculation.holiday.RecognizedHoliday;
import org.junit.Test;

import java.time.LocalDate;

import static junit.framework.TestCase.assertEquals;

public class TestDayCounts {
    @Test
    public void testIndependenceDay() {
        // Day of the fourth
        assertEquals(1, RecognizedHoliday.INDEPENDENCE_DAY.getDayCount(LocalDate.of(2014, 7, 4), LocalDate.of(2014, 7, 4)));

        // Day before the fourth
        assertEquals(0, RecognizedHoliday.INDEPENDENCE_DAY.getDayCount(LocalDate.of(2014, 7, 3), LocalDate.of(2014, 7, 3)));

        // Contains the fourth
        assertEquals(1, RecognizedHoliday.INDEPENDENCE_DAY.getDayCount(LocalDate.of(2014, 7, 3), LocalDate.of(2014, 7, 5)));

        // Over a year, containing two fourths
        assertEquals(2, RecognizedHoliday.INDEPENDENCE_DAY.getDayCount(LocalDate.of(2014, 7, 1), LocalDate.of(2015, 7, 7)));

        // Fourth on a Saturday
        assertEquals(0, RecognizedHoliday.INDEPENDENCE_DAY.getDayCount(LocalDate.of(2015, 7, 4), LocalDate.of(2015, 7, 4)));
        assertEquals(1, RecognizedHoliday.INDEPENDENCE_DAY.getDayCount(LocalDate.of(2015, 7, 3), LocalDate.of(2015, 7, 3)));

        // Fourth on a Sunday
        assertEquals(0, RecognizedHoliday.INDEPENDENCE_DAY.getDayCount(LocalDate.of(2021, 7, 4), LocalDate.of(2021, 7, 4)));
        assertEquals(1, RecognizedHoliday.INDEPENDENCE_DAY.getDayCount(LocalDate.of(2021, 7, 5), LocalDate.of(2021, 7, 5)));
    }

    @Test
    public void testLaborDay() {
        // Day of labor day
        assertEquals(1, RecognizedHoliday.LABOR_DAY.getDayCount(LocalDate.of(2015, 9, 7), LocalDate.of(2015, 9, 7)));

        // Day before labor day
        assertEquals(0, RecognizedHoliday.LABOR_DAY.getDayCount(LocalDate.of(2015, 9, 6), LocalDate.of(2015, 9, 6)));

        // Contains labor day
        assertEquals(1, RecognizedHoliday.LABOR_DAY.getDayCount(LocalDate.of(2015, 9, 1), LocalDate.of(2015, 9, 8)));

        // Over a year, containing two labor days
        assertEquals(2, RecognizedHoliday.LABOR_DAY.getDayCount(LocalDate.of(2015, 9, 1), LocalDate.of(2016, 9, 8)));
    }

    @Test
    public void testWeekdays() {
        // Single weekday
        assertEquals(1, Weekday.calculateWeekdays(LocalDate.of(2023, 7, 3), LocalDate.of(2023, 7, 3)));

        // Multiple weekdays
        assertEquals(3, Weekday.calculateWeekdays(LocalDate.of(2023, 7, 3), LocalDate.of(2023, 7, 5)));

        // A full week, minus weekend
        assertEquals(5, Weekday.calculateWeekdays(LocalDate.of(2023, 7, 3), LocalDate.of(2023, 7, 9)));

        // Single weekend day
        assertEquals(0, Weekday.calculateWeekdays(LocalDate.of(2023, 7, 2), LocalDate.of(2023, 7, 2)));

        // Two days of the weekend
        assertEquals(0, Weekday.calculateWeekdays(LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 2)));

        // Thursday to Monday, just for one more complicated rounding case
        assertEquals(3, Weekday.calculateWeekdays(LocalDate.of(2023, 7, 6), LocalDate.of(2023, 7, 10)));

        // End on a Saturday
        assertEquals(10, Weekday.calculateWeekdays(LocalDate.of(2023, 7, 3), LocalDate.of(2023, 7, 15)));

        // Start on a Saturday, end next Sunday
        assertEquals(5, Weekday.calculateWeekdays(LocalDate.of(2023, 7, 15), LocalDate.of(2023, 7, 23)));
    }
}
