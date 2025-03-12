package service;

import dataaccess.DataAccessException;
import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBUserDAO;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthTests {
    private DBAuthDAO authentication;

    @BeforeEach
    public void testReset(){
        authentication = new DBAuthDAO();

    }

    @Test
    @DisplayName("Create new auth token")
    public void testCreateUserAuth() throws DataAccessException, SQLException {
        AuthData authData = authentication.createUserAuth("fake_username");

        Assertions.assertEquals("fake_username", authData.username());
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertFalse(authData.authToken().isEmpty());
    }

    @Test
    @DisplayName("Delete auth token")
    public void testDeleteUserAuth() throws DataAccessException, SQLException {
        AuthData authData = authentication.createUserAuth("fake_username");
        authentication.deleteUserAuth(authData.authToken());

        Assertions.assertFalse(authentication.checkUserAuth(authData.authToken()));
    }

    @Test
    @DisplayName("auth token does not exist")
    public void testAuthNotFound() throws Exception{
        AuthData authData = authentication.createUserAuth("fake_username");
        Exception exception = assertThrows(Exception.class, () -> {
            authentication.deleteUserAuth("fake auth");
        });

        Assertions.assertEquals("User not authenticated", exception.getMessage());
    }
}
