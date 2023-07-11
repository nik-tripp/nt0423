import app.Storefront;
import models.DBConnection;
import org.junit.After;
import org.junit.Test;

import java.sql.SQLException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class ProofTests {

    @After
    public void tearDown() throws SQLException {
        DBConnection.getConnection().close();
    }

    @Test
    public void Test1() {
        try {
            Storefront.rent("JAKR", 5, 101, "09/03/15");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Discount percent must be between 0 and 100.", e.getMessage());
        }
    }

    @Test
    public void Test2() {
        final String expected = "Tool code: LADW\n" +
                                "Tool type: Ladder\n" +
                                "Tool brand: Werner\n" +
                                "Rental days: 3\n" +
                                "Check out date: 07/02/20\n" +
                                "Due date: 07/05/20\n" +
                                "Daily rental charge: $1.99\n" +
                                "Charge days: 2\n" +
                                "Pre-discount charge: $3.98\n" +
                                "Discount percent: 10%\n" +
                                "Discount amount: $0.40\n" +
                                "Final charge: $3.58";
        assertEquals(expected, Storefront.rent("LADW", 3, 10, "07/02/20").toString());
    }

    @Test
    public void Test3() {
        final String expected = "Tool code: CHNS\n" +
                                "Tool type: Chainsaw\n" +
                                "Tool brand: Stihl\n" +
                                "Rental days: 5\n" +
                                "Check out date: 07/02/15\n" +
                                "Due date: 07/07/15\n" +
                                "Daily rental charge: $1.49\n" +
                                "Charge days: 3\n" +
                                "Pre-discount charge: $4.47\n" +
                                "Discount percent: 25%\n" +
                                "Discount amount: $1.12\n" +
                                "Final charge: $3.35";
        assertEquals(expected, Storefront.rent("CHNS", 5, 25, "07/02/15").toString());
    }

    @Test
    public void Test4() {
        final String expected = "Tool code: JAKD\n" +
                                "Tool type: Jackhammer\n" +
                                "Tool brand: DeWalt\n" +
                                "Rental days: 6\n" +
                                "Check out date: 09/03/15\n" +
                                "Due date: 09/09/15\n" +
                                "Daily rental charge: $2.99\n" +
                                "Charge days: 3\n" +
                                "Pre-discount charge: $8.97\n" +
                                "Discount percent: 00%\n" +
                                "Discount amount: $0.00\n" +
                                "Final charge: $8.97";
        assertEquals(expected, Storefront.rent("JAKD", 6, 0, "09/03/15").toString());
    }

    @Test
    public void Test5() {
        final String expected = "Tool code: JAKR\n" +
                                "Tool type: Jackhammer\n" +
                                "Tool brand: Ridgid\n" +
                                "Rental days: 9\n" +
                                "Check out date: 07/02/15\n" +
                                "Due date: 07/11/15\n" +
                                "Daily rental charge: $2.99\n" +
                                "Charge days: 5\n" +
                                "Pre-discount charge: $14.95\n" +
                                "Discount percent: 00%\n" +
                                "Discount amount: $0.00\n" +
                                "Final charge: $14.95";
        assertEquals(expected, Storefront.rent("JAKR", 9, 0, "07/02/15").toString());
    }

    @Test
    public void Test6() {
        final String expected = "Tool code: JAKR\n" +
                                "Tool type: Jackhammer\n" +
                                "Tool brand: Ridgid\n" +
                                "Rental days: 4\n" +
                                "Check out date: 07/02/20\n" +
                                "Due date: 07/06/20\n" +
                                "Daily rental charge: $2.99\n" +
                                "Charge days: 1\n" +
                                "Pre-discount charge: $2.99\n" +
                                "Discount percent: 50%\n" +
                                "Discount amount: $1.50\n" +
                                "Final charge: $1.49";
        assertEquals(expected, Storefront.rent("JAKR", 4, 50, "07/02/20").toString());
    }
}
