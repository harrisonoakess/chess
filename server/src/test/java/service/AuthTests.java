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

public class AuthTests {
    private DBAuthDAO authentication;

    @BeforeEach
    public void testReset(){
        authentication = new DBAuthDAO();
    }

    @Test
    @DisplayName("Create new auth token")
    public void testCreateNewToken() throws DataAccessException{
        AuthData authData = authentication.createUserAuth("fake_username");

        Assertions.assertEquals("fake_username", authData.username());
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertFalse(authData.authToken().isEmpty());
    }
}
