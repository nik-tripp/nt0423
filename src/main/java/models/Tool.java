package models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Tool {
    private String _code;
    private String _type;
    private String _brand;
    private double _dailyCharge;
    private boolean _weekdayCharge;
    private boolean _weekendCharge;
    private boolean _holidayCharge;

    /**
     * Fetch a tool from the database by its code.
     * @param toolCode
     * @return the tool, or null if not found
     * @throws SQLException
     */
    public static Tool fetchByCode(String toolCode) throws SQLException {
        Tool ret = null;
        String type;
        String brand;
        double dailyCharge;
        boolean weekdayCharge;
        boolean weekendCharge;
        boolean holidayCharge;

        ResultSet tool = DBConnection.getConnection().createStatement().executeQuery("SELECT * FROM tool JOIN tool_type ON tool.type = tool_type.type WHERE code = '" + toolCode + "';");

        if (tool.next()) {
            type = tool.getString("type");
            brand = tool.getString("brand");
            dailyCharge = tool.getDouble("daily_charge");
            weekdayCharge = tool.getBoolean("weekday_charge");
            weekendCharge = tool.getBoolean("weekend_charge");
            holidayCharge = tool.getBoolean("holiday_charge");
            ret = new Tool(toolCode, type, brand, dailyCharge, weekdayCharge, weekendCharge, holidayCharge);
        }

        return ret;
    }

    private Tool() {
    }

    private Tool(String code, String type, String brand, double dailyCharge, boolean weekdayCharge, boolean weekendCharge, boolean holidayCharge) {
        _code = code;
        _type = type;
        _brand = brand;
        _dailyCharge = dailyCharge;
        _weekdayCharge = weekdayCharge;
        _weekendCharge = weekendCharge;
        _holidayCharge = holidayCharge;
    }

    public String getCode() {
        return _code;
    }

    public String getType() {
        return _type;
    }

    public String getBrand() {
        return _brand;
    }

    public double getDailyCharge() {
        return _dailyCharge;
    }

    public boolean isWeekdayCharge() {
        return _weekdayCharge;
    }

    public boolean isWeekendCharge() {
        return _weekendCharge;
    }

    public boolean isHolidayCharge() {
        return _holidayCharge;
    }
}
