import models.DBConnection;
import org.junit.After;
import org.junit.Test;

import java.sql.SQLException;

public class TestStorefront {
    @After
    public void tearDown() throws SQLException {
        DBConnection.getConnection().close();
    }

    @Test
    public void testRentTool() {
        // TODO
    }

    @Test
    public void testInvalidRentalDayCount() {
        // TODO
    }

    @Test
    public void testInvalidDiscountPercentage() {
        // TODO
    }
}
