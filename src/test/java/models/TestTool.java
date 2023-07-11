package models;

import org.junit.After;
import org.junit.Test;

import java.sql.SQLException;

import static junit.framework.TestCase.*;

public class TestTool {
    @After
    public void tearDown() throws SQLException {
        DBConnection.getConnection().close();
    }

    @Test
    public void testFetchByCode() throws SQLException {
        Tool tool = Tool.fetchByCode("JAKR");
        assertNotNull(tool);
        assertEquals("JAKR", tool.getCode());
        assertEquals("Jackhammer", tool.getType());
        assertEquals("Ridgid", tool.getBrand());
        assertEquals(2.99, tool.getDailyCharge());
        assertTrue(tool.isWeekdayCharge());
        assertFalse(tool.isWeekendCharge());
        assertFalse(tool.isHolidayCharge());
    }
}
