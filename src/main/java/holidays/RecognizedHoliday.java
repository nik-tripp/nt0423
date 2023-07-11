package holidays;

public enum RecognizedHoliday {
    INDEPENDENCE_DAY(new IndependenceDay()),
    LABOR_DAY(new LaborDay());

    private final Holiday holiday;
    RecognizedHoliday(Holiday holiday) {
        this.holiday = holiday;
    }
}
