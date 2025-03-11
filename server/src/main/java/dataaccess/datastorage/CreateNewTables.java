package dataaccess.datastorage;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateNewTables {

    private static final String CREATE_USER_TABLE =
            """
            CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(100) PRIMARY KEY,
            password VARCHAR(225) NOT NULL,
            email VARCHAR (100) NOT NULL
            )
            """;

    private static final String CREATE_GAME_TABLE =
            """
            CREATE TABLE IF NOT EXISTS games (
            gameID INT AUTO_INCREMENT PRIMARY KEY,
            whiteUsername VARCHAR(100),
            blackUsername VARCHAR(100),
            gameName VARCHAR(100) NOT NULL,
            game TEXT
            )
            """;
    private static final String CREATE_AUTH_TABLE =
            """
            CREATE TABLE IF NOT EXISTS auth (
            username VARCHAR(100) PRIMARY KEY,
            authToken VARCHAR(225) NOT NULL
            )            
            """;

    public static void initialize() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(CREATE_USER_TABLE)) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(CREATE_GAME_TABLE)) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(CREATE_AUTH_TABLE)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error initializing database: " + e.getMessage());
        }
    }
}