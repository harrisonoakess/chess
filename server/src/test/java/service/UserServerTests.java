package service;

import dataaccess.DataAccessException;
import dataaccess.datastorage.DBUserDAO;
import model.LoginRequest;
import model.LoginResult;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServerTests {
    private UserService userService;

    @BeforeEach
    public void testReset(){
        DBUserDAO memoryUserDAO = new DBUserDAO();
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

    @Test
    @DisplayName("Successful login")
    public void testSuccessfullyLogin() throws DataAccessException{
        RegisterRequest registerRequest = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("fake_username", "fake_password");
        LoginResult loginResult = userService.login(loginRequest);

        assertEquals(loginRequest.username(), loginResult.username());
    }
    @Test
    @DisplayName("User does not exist")
    public void testUsernameNotFound() throws DataAccessException{
        RegisterRequest registerRequest = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("fake_username_", "fake_password");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login(loginRequest);
        });
        assertEquals("User does not exists", exception.getMessage());
    }

    @Test
    @DisplayName("Incorrect password")
    public void testIncorrectPassword() throws DataAccessException{
        RegisterRequest registerRequest = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("fake_username", "fake_password_");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login(loginRequest);
        });
        assertEquals("Password does not match", exception.getMessage());
    }


}

