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
    public int getDayCount(LocalDate start, LocalDate end) {
        return _holiday.getDayCount(start, end);
    }
}
