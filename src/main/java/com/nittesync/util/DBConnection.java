package com.nittesync.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Creates JDBC connections for the whole application.
 *
 * The settings are read once from db.properties on the classpath
 * (src/main/resources/db.properties), so no credentials are hard coded
 * anywhere else in the code.
 *
 * Usage in a DAO:
 *
 *   try (Connection connection = DBConnection.getConnection();
 *        PreparedStatement statement = connection.prepareStatement(SQL)) {
 *       ...
 *   }
 */
public final class DBConnection {

    private static final String PROPERTIES_FILE = "db.properties";

    private static final Properties PROPERTIES = new Properties();

    /** Non-null when the configuration could not be loaded. */
    private static String configError;

    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                configError = "db.properties was not found on the classpath. It belongs in src/main/resources.";
            } else {
                PROPERTIES.load(input);
                // Optional with JDBC 4, but writing it down makes the driver
                // requirement visible and gives a clear error when it is missing.
                Class.forName(PROPERTIES.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
            }
        } catch (IOException e) {
            configError = "db.properties could not be read: " + e.getMessage();
        } catch (ClassNotFoundException e) {
            configError = "The MySQL JDBC driver is not on the classpath. Check the pom.xml and redeploy the WAR.";
        }
    }

    private DBConnection() {
        // utility class: never instantiated
    }

    /**
     * @return a new connection to the nitte_sync database
     * @throws SQLException when MySQL is unreachable or the credentials are wrong
     */
    public static Connection getConnection() throws SQLException {
        if (configError != null) {
            throw new SQLException(configError);
        }
        String url = PROPERTIES.getProperty("db.url", "");
        String user = PROPERTIES.getProperty("db.user", "");
        String password = PROPERTIES.getProperty("db.password", "");
        return DriverManager.getConnection(url, user, password);
    }

    /** The JDBC URL currently configured - used by the connection test. */
    public static String getUrl() {
        return PROPERTIES.getProperty("db.url", "(not configured)");
    }
}