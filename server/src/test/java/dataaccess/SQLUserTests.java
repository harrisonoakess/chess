package dataaccess;

import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBGameDAO;
import dataaccess.datastorage.DBUserDAO;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserTests {
    private DBAuthDAO authDAO;
    private DBUserDAO userDAO;
    private DBGameDAO gameDAO;

    @BeforeEach
    public void setup() throws DataAccessException, SQLException {
        authDAO = new DBAuthDAO();
        userDAO = new DBUserDAO(authDAO);
        gameDAO = new DBGameDAO();

        authDAO.clearAuths();
        userDAO.clearUsers();
        gameDAO.clearGames();
    }

    @Test
    @DisplayName("Account added successfully")
    public void testCreateUserSuccess() throws DataAccessException, SQLException {
        UserData user = new UserData("fake_username", "fake_password", "email@fake.gov");
        userDAO.createNewUser(user);

        UserData returned = userDAO.checkUser("fake_username");
        Assertions.assertEquals("fake_username", returned.username());
    }
    @Test
    @DisplayName("User does not exsist")
    public void testUserDoesNotExist() throws DataAccessException, SQLException {
        assertNull(userDAO.checkUser("fake_username"));
    }

    @Test
    @DisplayName("Clear users success")
    public void testClearUsers() throws DataAccessException, SQLException {
        UserData user = new UserData("fake_username", "fake_password", "email@fake.gov");
        userDAO.createNewUser(user);
        userDAO.clearUsers();

        assertNull(userDAO.checkUser("fake_username"));
    }
}
