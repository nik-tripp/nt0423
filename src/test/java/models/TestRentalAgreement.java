package models;

import org.junit.After;
import org.junit.Test;

import java.sql.SQLException;

public class TestRentalAgreement {
    @After
    public void tearDown() throws SQLException {
        DBConnection.getConnection().close();
    }

    @Test
    public void testToString() {
        // TODO
    }

    @Test
    public void testGetChargeDays() {
        // TODO
    }

    @Test
    public void testGetPreDiscountCharge() {
        // TODO
    }

    @Test
    public void testGetDiscountAmount() {
        // TODO
    }

    @Test
    public void testGetFinalCharge() {
        // TODO
    }

    @Test
    public void testDBInteraction() {
        // This is completely surplus to requirements, but it felt wrong to omit saving this to the database.
        // TODO
    }
}
