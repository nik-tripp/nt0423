package holidays;

import java.time.LocalDate;

public enum RecognizedHoliday {
    INDEPENDENCE_DAY(new IndependenceDay()),
    LABOR_DAY(new LaborDay());

    private final Holiday _holiday;
    RecognizedHoliday(Holiday holiday) {
        this._holiday = holiday;
    }
    public String getName() {
        return _holiday.getName();
    }

    /**
     * Returns the number of days within the given range that are observed as this holiday.
     * @param start
     * @param end
     * @return the count of days observed as this holiday
     */
    public int getDayCount(LocalDate start, LocalDate end) {
        return _holiday.getDayCount(start, end);
    }
}
