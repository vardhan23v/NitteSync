package com.nittesync.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Stand-alone check of the database configuration.
 *
 * Run it in Eclipse with:  right click the file -> Run As -> Java Application
 * or from the terminal with: mvn compile exec:java -Dexec.mainClass=com.nittesync.util.TestConnection
 *
 * It prints either the MySQL version or a readable explanation of what is
 * wrong, which makes it the fastest way to diagnose setup problems.
 */
public final class TestConnection {

    private TestConnection() {
        // only the main method is used
    }

    public static void main(String[] args) {
        System.out.println("NitteSync - database connection test");
        System.out.println("URL: " + DBConnection.getUrl());

        try (Connection connection = DBConnection.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("Connected successfully.");
            System.out.println("Database : " + metaData.getDatabaseProductName()
                    + " " + metaData.getDatabaseProductVersion());
            System.out.println("Driver   : " + metaData.getDriverName()
                    + " " + metaData.getDriverVersion());
        } catch (SQLException e) {
            System.out.println("Connection FAILED: " + e.getMessage());
            System.out.println("Check that MySQL is running and that db.properties matches your credentials.");
        }
    }
}