package models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class TestRentalAgreement {
    private RentalAgreement _rentalAgreement;

    @Before
    public void setUp() throws SQLException {
        _rentalAgreement = RentalAgreement.createNew("JAKD", 6, 10, "09/03/15");
    }

    @After
    public void tearDown() throws SQLException {
        DBConnection.getConnection().close();
    }

    @Test
    public void testToString() {
        final String expected = "Tool code: JAKD\n" +
                                "Tool type: Jackhammer\n" +
                                "Tool brand: DeWalt\n" +
                                "Rental days: 6\n" +
                                "Check out date: 09/03/15\n" +
                                "Due date: 09/09/15\n" +
                                "Daily rental charge: $2.99\n" +
                                "Charge days: 3\n" +
                                "Pre-discount charge: $8.97\n" +
                                "Discount percent: 10%\n" +
                                "Discount amount: $0.90\n" +
                                "Final charge: $8.07";
        assertEquals(expected, _rentalAgreement.toString());
    }

    @Test
    public void testGetChargeDays() {
        assertEquals(3, _rentalAgreement.getChargeDays());
    }

    @Test
    public void testGetPreDiscountCharge() {
        assertEquals(8.97, _rentalAgreement.getPreDiscountCharge(), 0.001);
    }

    @Test
    public void testGetDiscountAmount() {
        assertEquals(0.90, _rentalAgreement.getDiscountAmount(), 0.001);
    }

    @Test
    public void testGetFinalCharge() {
        assertEquals(8.07, _rentalAgreement.getFinalCharge(), 0.001);
    }

    @Test
    public void testDBInteraction() throws SQLException {
        // This is completely surplus to requirements, but it felt wrong to omit saving this to the database.
        assertEquals(_rentalAgreement, RentalAgreement.fetchByPK(_rentalAgreement.getPK()));
    }

    @Test
    public void testInvalidDays() {
        try {
            RentalAgreement.createNew("JAKR", 0, 0, "12/12/2012");
            fail("Allowed 0 day rental");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (RentalAgreement.IllegalRentalDaysException e) {
        }
    }

    @Test
    public void testInvalidDiscount() {
        try {
            RentalAgreement.createNew("JAKR", 1, 101, "12/12/2012");
            fail("Allowed 101% discount");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (RentalAgreement.IllegalDiscountPercentOver100Exception e) {
        }

        try {
            RentalAgreement.createNew("JAKR", 1, -1, "12/12/2012");
            fail("Allowed -1% discount");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (RentalAgreement.IllegalDiscountPercentNegativeException e) {
        }
    }
}
