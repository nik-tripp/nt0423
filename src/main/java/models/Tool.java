package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Tool {
    private static final String FETCH_BY_CODE = "SELECT * FROM tool JOIN tool_type ON tool.type = tool_type.type WHERE code = ?";
    private long _pk;
    private String _code;
    private String _type;
    private String _brand;
    private float _dailyCharge;
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
        PreparedStatement fetchByCode = DBConnection.getConnection().prepareStatement(FETCH_BY_CODE);

        fetchByCode.setString(1, toolCode);
        ResultSet tool = fetchByCode.executeQuery();

        return buildFromResultSet(tool);
    }

    private static Tool buildFromResultSet(ResultSet tool) throws SQLException {
        Tool ret = null;
        String code;
        String type;
        String brand;
        float dailyCharge;
        boolean weekdayCharge;
        boolean weekendCharge;
        boolean holidayCharge;

        if (tool.next()) {
            code = tool.getString("code");
            type = tool.getString("type");
            brand = tool.getString("brand");
            dailyCharge = tool.getFloat("daily_charge");
            weekdayCharge = tool.getBoolean("weekday_charge");
            weekendCharge = tool.getBoolean("weekend_charge");
            holidayCharge = tool.getBoolean("holiday_charge");
            ret = new Tool(code, type, brand, dailyCharge, weekdayCharge, weekendCharge, holidayCharge);
        }

        return ret;
    }

    private Tool() {
    }

    private Tool(String code, String type, String brand, float dailyCharge, boolean weekdayCharge, boolean weekendCharge, boolean holidayCharge) {
        _code = code;
        _type = type;
        _brand = brand;
        _dailyCharge = dailyCharge;
        _weekdayCharge = weekdayCharge;
        _weekendCharge = weekendCharge;
        _holidayCharge = holidayCharge;
    }

    public long getPK() {
        return _pk;
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

    public float getDailyCharge() {
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
