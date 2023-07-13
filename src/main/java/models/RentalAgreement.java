package models;

import daycalculation.Weekday;
import daycalculation.holiday.RecognizedHoliday;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class RentalAgreement {
    private static final String INSERT = "INSERT INTO rental_agreement (tool_code, tool_type, tool_brand, rental_days, check_out_date, due_date, charge_days, daily_charge, discount_percent) VALUES (?, ?, ?, ?, ?, ?, ? , ?, ?)";
    private static final String FETCH_BY_ID = "SELECT rowid, * FROM rental_agreement WHERE rowid = ?";
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("0.00");
    static {
        CURRENCY_FORMAT.setRoundingMode(java.math.RoundingMode.HALF_UP);
    }
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yy");

    private long _pk;
    private String _toolCode;
    private String _toolType;
    private String _toolBrand;
    private int _rentalDays;
    private LocalDate _checkoutDate;
    private LocalDate _dueDate;
    private int _chargeDays;
    private float _dailyCharge;
    private int _discountPercent;

    // This could also be calculated on demand, except that the rounding requirements make the process more complex
    private float _discountAmount;

    private RentalAgreement(){}

    private RentalAgreement(Long pk, String toolCode, String toolType, String toolBrand, int rentalDays, LocalDate checkoutDate, LocalDate dueDate, int chargeDays, float dailyCharge, int discountPercent) {
        _pk = pk;
        _toolCode = toolCode;
        _toolType = toolType;
        _toolBrand = toolBrand;
        _rentalDays = rentalDays;
        _checkoutDate = checkoutDate;
        _dueDate = dueDate;
        _chargeDays = chargeDays;
        _dailyCharge = dailyCharge;
        _discountPercent = discountPercent;
        _discountAmount = Float.parseFloat(CURRENCY_FORMAT.format(getPreDiscountCharge() * _discountPercent / 100.0f));
    }

    /**
     * Create a new RentalAgreement and store it in the database.
     *
     * @param toolCode
     * @param rentalDays
     * @param discountPercent
     * @param checkoutDateString
     * @return the newly created RentalAgreement
     * @throws SQLException
     * @throws IllegalRentalDaysException if rentalDays is less than 1
     * @throws IllegalDiscountPercentNegativeException if discountPercent is less than 0
     * @throws IllegalDiscountPercentOver100Exception if discountPercent is greater than 100
     */
    public static RentalAgreement createNew(String toolCode, int rentalDays, int discountPercent, String checkoutDateString)
            throws SQLException, IllegalRentalDaysException, IllegalDiscountPercentNegativeException, IllegalDiscountPercentOver100Exception
    {
        if (rentalDays < 1) {
            throw new IllegalRentalDaysException();
        }
        if (discountPercent < 0) {
            throw new IllegalDiscountPercentNegativeException();
        }
        if (discountPercent > 100) {
            throw new IllegalDiscountPercentOver100Exception();
        }

        Tool tool = Tool.fetchByCode(toolCode);
        LocalDate checkoutDate = LocalDate.parse(checkoutDateString, DateTimeFormatter.ofPattern("MM/dd/yy"));
        LocalDate dueDate = checkoutDate.plusDays(rentalDays);
        int chargeDays = calculateChargeDays(tool, rentalDays, checkoutDate);
        long pk;

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, tool.getCode());
            statement.setString(2, tool.getType());
            statement.setString(3, tool.getBrand());
            statement.setInt(4, rentalDays);
            statement.setString(5, checkoutDate.format(DateTimeFormatter.ISO_DATE));
            statement.setString(6, dueDate.format(DateTimeFormatter.ISO_DATE));
            statement.setInt(7, chargeDays);
            statement.setFloat(8, tool.getDailyCharge());
            statement.setInt(9, discountPercent);
            statement.execute();
            pk = statement.getGeneratedKeys().getLong(1);
        }

        return new RentalAgreement(pk, tool.getCode(), tool.getType(), tool.getBrand(), rentalDays, checkoutDate, dueDate, chargeDays, tool.getDailyCharge(), discountPercent);
    }

    /**
     * Fetch a RentalAgreement from the database by its row's PK.
     * @param pk
     * @return the RentalAgreement with the given PK, or null if no such RentalAgreement exists
     * @throws SQLException
     */
    public static RentalAgreement fetchByPK(long pk) throws SQLException {
        RentalAgreement ret = null;
        String toolCode;
        String toolType;
        String toolBrand;
        int rentalDays;
        LocalDate checkoutDate;
        LocalDate dueDate;
        int chargeDays;
        float dailyCharge;
        int discountPercent;

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(FETCH_BY_ID)) {
            statement.setLong(1, pk);
            statement.execute();
            ResultSet result = statement.getResultSet();
            if (result.next()) {
                toolCode = result.getString("tool_code");
                toolType = result.getString("tool_type");
                toolBrand = result.getString("tool_brand");
                rentalDays = result.getInt("rental_days");
                checkoutDate = LocalDate.parse(result.getString("check_out_date"));
                dueDate = LocalDate.parse(result.getString("due_date"));
                chargeDays = result.getInt("charge_days");
                dailyCharge = result.getFloat("daily_charge");
                discountPercent = result.getInt("discount_percent");
                ret = new RentalAgreement(pk, toolCode, toolType, toolBrand, rentalDays, checkoutDate, dueDate, chargeDays, dailyCharge, discountPercent);
            }
        }

        return ret;
    }

    public static int calculateChargeDays(Tool tool, int rentalDays, LocalDate checkoutDate) {
        // Charges are for days after checkout, including the checkout day
        LocalDate firstChargeDay = checkoutDate.plusDays(1);
        LocalDate finalChargeDay = checkoutDate.plusDays(rentalDays);

        // Assume tool is charged for all days of rental
        int chargeDays = rentalDays;

        // Only bother with calculating weekdays if that sort of thing matters for the tool
        if (!tool.isWeekdayCharge() || !tool.isWeekendCharge()) {
            long weekdays = Weekday.calculateWeekdays(firstChargeDay, finalChargeDay);

            // Remove weekdays, weekends, or both if necessary
            if (!tool.isWeekdayCharge()) {
                chargeDays -= weekdays;
            }
            if (!tool.isWeekendCharge()) {
                chargeDays -= rentalDays - weekdays;
            }
        }

        // Remove holidays, if necessary
        if (!tool.isHolidayCharge() && chargeDays > 0) {
            for (RecognizedHoliday holiday : RecognizedHoliday.values()) {
                chargeDays -= holiday.getDayCount(firstChargeDay, finalChargeDay);
            }
        }

        return chargeDays;
    }

    public long getPK() {
        return _pk;
    }

    public String getToolCode() {
        return _toolCode;
    }

    public String getToolType() {
        return _toolType;
    }

    public String getToolBrand() {
        return _toolBrand;
    }

    public int getRentalDays() {
        return _rentalDays;
    }

    public LocalDate getCheckoutDate() {
        return _checkoutDate;
    }

    public LocalDate getDueDate() {
        return _dueDate;
    }

    public float getDailyRentalCharge() {
        return _dailyCharge;
    }

    public int getChargeDays() {
        return _chargeDays;
    }

    public float getPreDiscountCharge() {
        return _chargeDays * _dailyCharge;
    }

    public int getDiscountPercent() {
        return _discountPercent;
    }

    public float getDiscountAmount() {
        return _discountAmount;
    }

    public float getFinalCharge() {
        return getPreDiscountCharge() - getDiscountAmount();
    }

    public void printReceipt() {
        System.out.println(this);
    }

    /**
     * Create a receipt string for this RentalAgreement to the given PrintStream.
     * @return a String representation of all parameters of this RentalAgreement with the following format:
     * Tool code: ****
     * Tool type: ****
     * ...
     * Final charge: $9.99
     * with formatting as follows:
     * ● Date mm/dd/yy
     * ● Currency $9,999.99
     * ● Percent 99%
     */
    public String toString() {
        return "Tool code: " + getToolCode() + "\n" +
               "Tool type: " + getToolType() + "\n" +
               "Tool brand: " + getToolBrand() + "\n" +
               "Rental days: " + getRentalDays() + "\n" +
               "Check out date: " + getCheckoutDate().format(DATE_FORMAT) + "\n" +
               "Due date: " + getDueDate().format(DATE_FORMAT) + "\n" +
               "Daily rental charge: $" + CURRENCY_FORMAT.format(getDailyRentalCharge()) + "\n" +
               "Charge days: " + getChargeDays() + "\n" +
               "Pre-discount charge: $" + CURRENCY_FORMAT.format(getPreDiscountCharge()) + "\n" +
               "Discount percent: " + String.format("%02d", getDiscountPercent()) + "%\n" +
               "Discount amount: $" + CURRENCY_FORMAT.format(getDiscountAmount()) + "\n" +
               "Final charge: $" + CURRENCY_FORMAT.format(getFinalCharge());
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RentalAgreement)) {
            return false;
        }
        RentalAgreement other = (RentalAgreement)o;
        return _pk == other._pk &&
               _toolCode.equals(other._toolCode) &&
               _toolType.equals(other._toolType) &&
               _toolBrand.equals(other._toolBrand) &&
               _rentalDays == other._rentalDays &&
               _checkoutDate.equals(other._checkoutDate) &&
               _dueDate.equals(other._dueDate) &&
               _chargeDays == other._chargeDays &&
               _dailyCharge == other._dailyCharge &&
               _discountPercent == other._discountPercent;
    }

    public int hashCode() {
        return Objects.hash(_pk, _toolCode, _toolType, _toolBrand, _rentalDays, _checkoutDate, _dueDate, _chargeDays, _dailyCharge, _discountPercent);
    }

    public static class IllegalRentalDaysException extends IllegalArgumentException {
        public IllegalRentalDaysException() {
            super("Rental days must be greater than 0");
        }
    }
    public static class IllegalDiscountPercentNegativeException extends IllegalArgumentException {
        public IllegalDiscountPercentNegativeException() {
            super("Discount percent must be greater than or equal to 0");
        }
    }
    public static class IllegalDiscountPercentOver100Exception extends IllegalArgumentException {
        public IllegalDiscountPercentOver100Exception() {
            super("Discount percent must be less than or equal to 100");
        }
    }
}
