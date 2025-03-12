package dataaccess;

import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBGameDAO;
import dataaccess.datastorage.DBUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNull;
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
    @DisplayName("auth token does exist")
    public void testAuthFound() throws DataAccessException, SQLException{
        AuthData authData = authDAO.createUserAuth("fake_username");
        Assertions.assertTrue(authDAO.checkUserAuth(authData.authToken()));
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
    @Test
    @DisplayName("Returns username successfully")
    public void testReturnUsernameSuccessful() throws DataAccessException, SQLException{
        AuthData authData = authDAO.createUserAuth("fake_username");
        Assertions.assertEquals("fake_username", authDAO.returnUsername(authData.authToken()));
    }
    @Test
    @DisplayName("Clear auths success")
    public void testAuthTokens() throws DataAccessException, SQLException {
        authDAO.createUserAuth("fake_user");
        authDAO.clearAuths();

        assertNull(userDAO.checkUser("fake_username"));
    }
}
