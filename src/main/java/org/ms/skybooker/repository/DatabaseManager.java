package org.ms.skybooker.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DRIVER_CLASS = "org.sqlite.JDBC";
    private static final String DATABASE_URL = "jdbc:sqlite:src/main/java/org/ms/skybooker/repository/flightDatabase.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER_CLASS);
            return DriverManager.getConnection(DATABASE_URL);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createDatabase() {
        try {
            Class.forName(DRIVER_CLASS);
            Connection conn = DriverManager.getConnection(DATABASE_URL);
            if (conn != null) {
                Statement statement = conn.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS tabela (id INTEGER PRIMARY KEY, name TEXT)";
                statement.execute(sql);
                conn.close();
                System.out.println("Baza danych została utworzona.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}

