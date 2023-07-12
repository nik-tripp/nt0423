package app;

import models.RentalAgreement;

import java.sql.SQLException;

public class Storefront {
    private static final String INVALID_RENTAL_DAYS = "Tools cannot be checked out for less than one day.";
    private static final String INVALID_DISCOUNT_PERCENT_NEG = "Discount percent cannot be negative.";
    private static final String INVALID_DISCOUNT_PERCENT_OVER_100 = "Discount percent cannot be over 100.";

    /**
     * Generate a RentalAgreement for a tool rental.
     * @param toolCode
     * @param rentalDayCount
     * @param discountPercent
     * @param checkoutDate
     * @return the RentalAgreement
     * @throws SQLException
     */
    public static RentalAgreement rent(String toolCode, int rentalDayCount, int discountPercent, String checkoutDate) throws SQLException {
        RentalAgreement ret;

        try {
            ret = RentalAgreement.createNew(toolCode, rentalDayCount, discountPercent, checkoutDate);
        } catch (RentalAgreement.IllegalRentalDaysException e) {
            throw new IllegalArgumentException(INVALID_RENTAL_DAYS);
        } catch (RentalAgreement.IllegalDiscountPercentNegativeException e) {
            throw new IllegalArgumentException(INVALID_DISCOUNT_PERCENT_NEG);
        } catch (RentalAgreement.IllegalDiscountPercentOver100Exception e) {
            throw new IllegalArgumentException(INVALID_DISCOUNT_PERCENT_OVER_100);
        }

        return ret;
    }
}
