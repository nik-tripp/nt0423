package models;

import org.junit.After;
import org.junit.Test;

import java.sql.SQLException;

public class TestTool {
    @After
    public void tearDown() throws SQLException {
        DBConnection.getConnection().close();
    }

    @Test
    public void testFetchByCode() {
        // TODO
    }
}
