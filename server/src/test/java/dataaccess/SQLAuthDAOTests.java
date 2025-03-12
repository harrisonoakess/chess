package dataaccess;

import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBGameDAO;
import dataaccess.datastorage.DBUserDAO;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLAuthDAOTests {
    private DBAuthDAO authDAO;
    private DBUserDAO userDAO;
    private DBGameDAO gameDAO;

    @BeforeEach
    public void testReset() throws DataAccessException, SQLException {
        authDAO = new DBAuthDAO();
        userDAO = new DBUserDAO(authDAO);
        gameDAO = new DBGameDAO();

        authDAO.clearAuths();
        userDAO.clearUsers();
        gameDAO.clearGames();
    }

    @Test
    @DisplayName("Create new auth token")
    public void testCreateUserAuth() throws DataAccessException, SQLException {
        AuthData authData = authDAO.createUserAuth("fake_username");

        Assertions.assertEquals("fake_username", authData.username());
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertFalse(authData.authToken().isEmpty());
    }

    @Test
    @DisplayName("Delete auth token")
    public void testDeleteUserAuth() throws DataAccessException, SQLException {
        AuthData authData = authDAO.createUserAuth("fake_username");
        authDAO.deleteUserAuth(authData.authToken());

        Assertions.assertFalse(authDAO.checkUserAuth(authData.authToken()));
    }

    @Test
    @DisplayName("auth token does not exist")
    public void testAuthNotFound() throws Exception{
        AuthData authData = authDAO.createUserAuth("fake_username");
        Exception exception = assertThrows(Exception.class, () -> {
            authDAO.deleteUserAuth("fake auth");
        });

        Assertions.assertEquals("User not authenticated", exception.getMessage());
    }
}
