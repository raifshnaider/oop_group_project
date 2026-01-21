package backend.util;

import java.sql.Connection;
import java.sql.SQLException;

public class DbUtils {
    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
