import app.Storefront;
import models.DBConnection;
import org.junit.After;
import org.junit.Test;

import java.sql.SQLException;

import static junit.framework.TestCase.*;

public class TestStorefront {
    @After
    public void tearDown() throws SQLException {
        DBConnection.getConnection().close();
    }

    @Test
    public void testRentTool() throws SQLException {
        assertNotNull(Storefront.rent("JAKR", 5, 50, "09/03/15"));
    }

    @Test
    public void testInvalidRentalDayCount() throws SQLException {
        try {
            Storefront.rent("JAKR", 0, 20, "09/03/15");
            fail("Expected zero rental days to throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Tools cannot be checked out for less than one day.", e.getMessage());
        }
    }

    @Test
    public void testInvalidDiscountPercentage() throws SQLException {
        try {
            Storefront.rent("JAKR", 3, -5, "09/03/15");
            fail("Expected negative discount percent to throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Discount percent cannot be negative.", e.getMessage());
        }

        try {
            Storefront.rent("JAKR", 3, 101, "09/03/15");
            fail("Expected 101 discount percent to throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Discount percent cannot be over 100.", e.getMessage());
        }
    }
}
