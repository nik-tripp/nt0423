package models;

import holidays.RecognizedHoliday;

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
    private static final String INSERT = "INSERT INTO rental_agreement (tool_id, rental_days, check_out_date, discount_percent) VALUES (?, ?, ?, ?)";
    private static final String FETCH_BY_ID = "SELECT rowid, * FROM rental_agreement WHERE rowid = ?";
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("0.00");
    static {
        CURRENCY_FORMAT.setRoundingMode(java.math.RoundingMode.HALF_UP);
    }
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yy");

    private long _pk;
    private Tool _tool;
    private int _rentalDays;
    private LocalDate _checkoutDate;

    // This could be calculated in a getter, but doing date math on demand seems like a poor decision
    private LocalDate _dueDate;

    // This could be calculated in a getter, but without caching we could end up running through expensive holiday
    // checks more times than I would like, so this is a simpler solution
    private int _chargeDays;
    private int _discountPercent;

    // This could also be calculated on demand, except that the rounding requirements make the process more complex
    private float _discountAmount;

    private RentalAgreement(){}

    private RentalAgreement(Long pk, Tool tool, int rentalDays, LocalDate checkoutDate, LocalDate dueDate, int discountPercent) {
        _pk = pk;
        _tool = tool;
        _rentalDays = rentalDays;
        _checkoutDate = checkoutDate;
        _dueDate = dueDate;
        _discountPercent = discountPercent;
        // Charges are for days after checkout, including due date, so calculate based on the day after checkout and the
        // day after the due date (so that the due date itself is included)
        LocalDate calcStartDay = checkoutDate.plusDays(1);
        LocalDate calcEndDay = dueDate.plusDays(1);
        int chargeDays = rentalDays;

        // Only bother with calculating weekdays if that sort of thing matters for the tool
        if (!_tool.isWeekdayCharge() || !_tool.isWeekendCharge()) {
            // Weekday calculations from https://stackoverflow.com/questions/4600034/calculate-number-of-weekdays-between-two-dates-in-java
            DayOfWeek startW = calcStartDay.getDayOfWeek();
            DayOfWeek endW = calcEndDay.getDayOfWeek();

            // Remove weekends
            int weekdays = rentalDays - (2 * (rentalDays / 7));

            // Handle edge days
            if (rentalDays % 7 != 0) {
                // If we started or ended on a sunday, there was one extra weekend day
                if (startW == DayOfWeek.SUNDAY || endW == DayOfWeek.SUNDAY) {
                    weekdays--;
                } else if (endW.getValue() < startW.getValue()) { // If we started later in the week than we ended, there was a whole extra weekend
                    weekdays -= 2;
                }
            }

            // Remove weekdays or weekends, whichever is necessary
            if (!_tool.isWeekdayCharge()) {
                chargeDays -= weekdays;
            }
            else {
                chargeDays -= rentalDays - weekdays;
            }
        }

        // Remove holidays, if necessary
        if (!tool.isHolidayCharge()) {
            for (RecognizedHoliday holiday : RecognizedHoliday.values()) {
                chargeDays -= holiday.getDayCount(calcStartDay, calcEndDay);
            }
        }

        _chargeDays = chargeDays;
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
        long pk;

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, tool.getPK());
            statement.setInt(2, rentalDays);
            statement.setString(3, checkoutDate.format(DateTimeFormatter.ISO_DATE));
            statement.setInt(4, discountPercent);
            statement.execute();
            pk = statement.getGeneratedKeys().getLong(1);
        }

        return new RentalAgreement(pk, tool, rentalDays, checkoutDate, dueDate, discountPercent);
    }

    /**
     * Fetch a RentalAgreement from the database by its row's PK.
     * @param pk
     * @return the RentalAgreement with the given PK, or null if no such RentalAgreement exists
     * @throws SQLException
     */
    public static RentalAgreement fetchByPK(long pk) throws SQLException {
        RentalAgreement ret = null;
        Tool tool;
        int rentalDays;
        LocalDate checkoutDate;
        LocalDate dueDate;
        int discountPercent;

        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(FETCH_BY_ID)) {
            statement.setLong(1, pk);
            statement.execute();
            ResultSet result = statement.getResultSet();
            if (result.next()) {
                tool = Tool.fetchByPK(result.getLong("tool_id"));
                rentalDays = result.getInt("rental_days");
                checkoutDate = LocalDate.parse(result.getString("check_out_date"));
                dueDate = checkoutDate.plusDays(rentalDays);
                discountPercent = result.getInt("discount_percent");
                ret = new RentalAgreement(pk, tool, rentalDays, checkoutDate, dueDate, discountPercent);
            }
        }

        return ret;
    }

    public long getPK() {
        return _pk;
    }

    public String getToolCode() {
        return _tool.getCode();
    }

    public String getToolType() {
        return _tool.getType();
    }

    public String getToolBrand() {
        return _tool.getBrand();
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
        return _tool.getDailyCharge();
    }

    public int getChargeDays() {
        return _chargeDays;
    }

    public float getPreDiscountCharge() {
        return _chargeDays * _tool.getDailyCharge();
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
               _tool.getPK() == other._tool.getPK() &&
               _rentalDays == other._rentalDays &&
               _checkoutDate.equals(other._checkoutDate) &&
               _dueDate.equals(other._dueDate) &&
               _discountPercent == other._discountPercent;
    }

    public int hashCode() {
        return Objects.hash(_pk, _tool, _rentalDays, _checkoutDate, _dueDate, _discountPercent);
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
