import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String dbURL = readConfig("DB_URL", "jdbc:mysql://localhost:3306/result_management");
    private static final String dbUser = readConfig("DB_USER", "root");
    private static final String dbPassword = readConfig("DB_PASSWORD", "root");

    public static Connection getDbConnection() {
        try {
            return DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String readConfig(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }
}

