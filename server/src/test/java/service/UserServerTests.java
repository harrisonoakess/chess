package service;

import dataaccess.DataAccessException;
import dataaccess.datastorage.MemoryUserDAO;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServerTests {
    private UserService userService;

    @BeforeEach
    public void testReset(){
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        userService = new UserService(memoryUserDAO);
    }

    @Test
    @DisplayName("Account added successfully")
    public void testRegisterSuccess() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        RegisterResult result = userService.register(request);

        Assertions.assertEquals("fake_username", result.username());
    }
    @Test
    @DisplayName("User already exists")
    public void testUserAlreadyExists() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(request);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
        assertEquals("Error: already taken", exception.getMessage());
    }

    @Test
    @DisplayName("Password left blank")
    public void testBlankPassword() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("fake_username", null, "email@fake.gov");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
        assertEquals("Password cannot be blank", exception.getMessage());
    }

}

