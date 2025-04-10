package util;

import java.sql.Connection;
import java.sql.SQLException;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.println("Connection successful!");
        } catch (DataAccessException | SQLException e) {
            System.err.println("Error connecting to the database:");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
